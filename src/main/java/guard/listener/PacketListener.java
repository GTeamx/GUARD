package guard.listener;

import guard.Guard;
import guard.check.GuardCheck;
import guard.data.GuardPlayer;
import guard.data.GuardPlayerManager;
import guard.runnable.RunnableTransaction;
import guard.utils.BoundingBox;
import guard.utils.packet.TransactionPacketClient;
import guard.utils.packet.TransactionPacketServer;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.PacketListenerAbstract;
import io.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.in.pong.WrappedPacketInPong;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.packetwrappers.play.out.entityvelocity.WrappedPacketOutEntityVelocity;
import io.github.retrooper.packetevents.packetwrappers.play.out.position.WrappedPacketOutPosition;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.function.Predicate;

public class PacketListener extends PacketListenerAbstract {

    public PacketListener() {
        super(PacketListenerPriority.HIGHEST);
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        Player p = event.getPlayer();
        GuardPlayerManager.addGuardPlayer(p);
        GuardPlayer gp = GuardPlayerManager.getGuardPlayer(p);
        NMSPacket packet = event.getNMSPacket();

        if(gp != null) {
            if (event.getPacketId() == PacketType.Play.Client.KEEP_ALIVE) {
                gp.ping = (int) (System.currentTimeMillis() - gp.serverKeepAlive);
            }
            if (p.isDead()) {
                gp.isDead = true;
                gp.wasDead = System.currentTimeMillis();
            } else {
                if (gp.isDead) {
                    gp.wasDead = System.currentTimeMillis();
                }
                gp.isDead = false;
            }



            if (event.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
                WrappedPacketInUseEntity ue = new WrappedPacketInUseEntity(packet);
                gp.target = ue.getEntity();
                gp.useAction = ue.getAction();
                if(ue.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                    gp.lastAttack = System.currentTimeMillis();
                }
            }
            gp.predictionProcessor.handle(event);
            for (GuardCheck c : gp.getCheckManager().checks) {
                c.gp = gp;
                c.onPacket(event);
            }
            if (event.getPacketId() == PacketType.Play.Client.TRANSACTION) {
                if (gp.packetTracker != null) {
                    gp.packetTracker.setIntervalPackets(0);
                }
                WrappedPacketInTransaction trans = new WrappedPacketInTransaction(event.getNMSPacket());
                boolean found = false;
                long foundTransactionTime = System.currentTimeMillis();
                TransactionPacketServer toRemove = null;
                for (TransactionPacketServer server : gp.transactions) {
                    if (server.transaction.getWindowId() == trans.getWindowId()) {
                        found = true;
                        foundTransactionTime = server.getTimeStamp();
                        toRemove = server;
                    }
                }
                long now = System.currentTimeMillis();
                if (toRemove != null)
                    gp.transactions.remove(toRemove);
                if(found) {
                    gp.transactionPackets.add((int) (now - foundTransactionTime));

                    if(gp.transactionPackets.isCollected()) {
                        gp.transactionPing = gp.transactionPackets.getAverageInt(gp.transactionPackets);
                    }
                }
                for (GuardCheck c : gp.getCheckManager().checks) {
                    c.gp = gp;
                    c.onTransaction(new TransactionPacketClient(trans, now), found);
                }
            }
            if (event.getPacketId() == PacketType.Play.Client.PONG) {
                if (gp.packetTracker != null) {
                    gp.packetTracker.setIntervalPackets(0);
                }
                WrappedPacketInPong pong = new WrappedPacketInPong(packet);
                boolean found = false;
                TransactionPacketServer toRemove = null;
                long foundTransactionTime = System.currentTimeMillis();
                try {
                    for (TransactionPacketServer server : gp.transactions) {
                        if (server.ping.getId() == pong.getId()) {
                            found = true;
                            toRemove = server;
                            foundTransactionTime = server.getTimeStamp();
                        }
                    }
                    long now = System.currentTimeMillis();
                    if (toRemove != null)
                        gp.transactions.remove(toRemove);
                    if (found) {
                        gp.transactionPackets.add((int) (now - foundTransactionTime));

                        if (gp.transactionPackets.isCollected()) {
                            gp.transactionPing = gp.transactionPackets.getAverageInt(gp.transactionPackets);
                        }
                    }
                    for (GuardCheck c : gp.getCheckManager().checks) {
                        c.gp = gp;
                        c.onTransaction(new TransactionPacketClient(pong, now), found);
                    }
                }catch (ConcurrentModificationException stfu) {}
            }
            if (event.getPacketId() == PacketType.Play.Client.BLOCK_DIG) {
                WrappedPacketInBlockDig dig = new WrappedPacketInBlockDig(packet);
                if (dig.getDigType().toString().contains("START_DESTROY_BLOCK")) {
                    gp.sentStartDestroy = true;
                }
                if (dig.getDigType().toString().contains("ABORT_DESTROY_BLOCK")) {
                    gp.sentStartDestroy = false;
                }
                if (dig.getDigType().toString().contains("STOP_DESTROY_BLOCK")) {
                    if (gp.sentStartDestroy) {
                        if (dig.getDirection().toString().contains("UP")) {
                            gp.brokeBlock = System.currentTimeMillis();
                        }
                    }
                    gp.sentStartDestroy = false;
                }
            }
            if (event.getPacketId() == PacketType.Play.Client.POSITION || event.getPacketId() == PacketType.Play.Client.POSITION_LOOK) {
                WrappedPacketInFlying ps = new WrappedPacketInFlying(packet);
                Location from = new Location(p.getWorld(), ps.getPosition().getX(), ps.getPosition().getY(), ps.getPosition().getZ());

                if(gp.to != null) {
                    if (PacketEvents.get().getPlayerUtils().getClientVersion(p).isNewerThanOrEquals(ClientVersion.v_1_17) && ps.isPosition() && ps.isLook() && sameLocation(gp, ps)) {
                        gp.noCheckNextFlying = true;
                    }
                }

                if(!gp.noCheckNextFlying) {
                    new RunnableTransaction(gp.player).runTaskAsynchronously(Guard.instance);
                    if (ps.isRotating()) {
                        from.setYaw(ps.getYaw());
                        from.setPitch(ps.getPitch());
                    } else {
                        from.setYaw(p.getLocation().getYaw());
                        from.setPitch(p.getLocation().getPitch());
                    }

                    if (gp.teleports.size() > 0) {
                        gp.teleportTickFix = 2;
                        gp.isTeleporting = true;
                    }

                    if (gp.teleports.size() == 0) {
                        if (--gp.teleportTickFix < 0) {
                            gp.isTeleporting = false;
                        }
                    }
                    if (gp.isTeleporting) {
                        gp.ticks++;
                    } else {
                        gp.ticks = 0;
                    }
                    if (gp.onSlime) {
                        gp.sinceSlimeTicks = 0;
                    } else {
                        gp.sinceSlimeTicks++;
                    }


                    gp.lastLastPlayerGround = gp.lastPlayerGround;
                    gp.lastPlayerGround = gp.playerGround;
                    gp.playerGround = ps.isOnGround();
                    gp.serverGround = from.clone().getY() % 0.015625 == 0.0;
                    gp.from = gp.to;
                    gp.to = from;
                    gp.sFrom = gp.sTo;
                    gp.sTo = from;
                    gp.doMove();
                    if (ps.isRotating()) {
                        float yawAccelAccel = Math.abs(gp.lastAccelYaw - Math.abs(Math.abs(gp.deltaYaw) - Math.abs(gp.lastDeltaYaw)));
                        float pitchAccelAccel = Math.abs(gp.lastAccelPitch - Math.abs(Math.abs(gp.deltaPitch) - Math.abs(gp.lastDeltaPitch)));
                        if ((String.valueOf(yawAccelAccel).contains("E") || String.valueOf(pitchAccelAccel).contains("E")) && gp.sensitivity < 100) {
                            gp.cinematicTicks += 3;
                        } else if (yawAccelAccel < .06 && yawAccelAccel > 0 || pitchAccelAccel < .06 && pitchAccelAccel > 0) {
                            gp.cinematicTicks += 1;
                        } else if (gp.cinematicTicks > 0) gp.cinematicTicks--;
                        if (gp.cinematicTicks > 20) gp.cinematicTicks--;
                        gp.isCinematic = gp.cinematicTicks > 8 || System.currentTimeMillis() - gp.lastCinematic < 250;
                        if (gp.cinematicTicks > 8 && gp.isCinematic) gp.lastCinematic = System.currentTimeMillis();
                        gp.lastAccelPitch = Math.abs(Math.abs(gp.deltaPitch) - Math.abs(gp.lastDeltaPitch));
                        gp.lastAccelYaw = Math.abs(Math.abs(gp.deltaYaw) - Math.abs(gp.lastDeltaYaw));
                        if (Math.abs(gp.deltaPitch) > 0 && Math.abs(gp.deltaPitch) < 30) {
                            float gcd = (float) gp.getGCD(Math.abs(gp.deltaPitch), Math.abs(gp.lastDeltaPitch));
                            double modifier = Math.cbrt(0.8333 * gcd);
                            double nextStep = (modifier / .6) - 0.3333;
                            double lastStep = nextStep * 200;
                            gp.sensitivitySamples.add((int) lastStep);
                            if (gp.sensitivitySamples.size() >= 20) {
                                gp.sensitivity = getMode(gp.sensitivitySamples);
                                float gcdOne = (gp.sensitivity / 200F) * 0.6F + 0.2F;
                                gp.realGCD = gcdOne * gcdOne * gcdOne * 1.2F;
                                gp.sensitivitySamples.clear();
                            }
                        }
                    }
                    handleBlocks(gp);
                    if (gp.getPlayer().isFlying()) {
                        gp.lastFlyingTime = System.currentTimeMillis();
                    }
                    if (gp.teleports.size() > 150) {
                        gp.teleports.remove(0);
                    }
                    if (gp.ticks > 50) {
                        gp.teleports.clear();
                    }
                    if (!gp.inAnimation) {
                        try {
                            for (Vector vector : gp.teleports) {
                                final double dx = Math.abs(from.getX() - vector.getX());
                                final double dy = Math.abs(from.getY() - vector.getY());
                                final double dz = Math.abs(from.getZ() - vector.getZ());

                                if (dx == 0 && dy == 0 && dz == 0) {
                                    gp.teleports.remove(vector);
                                }
                            }
                        } catch (ConcurrentModificationException ignored) {}
                    } else {
                        gp.teleports.clear();
                        gp.isTeleporting = true;
                    }

                    for (GuardCheck c : gp.getCheckManager().checks) {
                        c.gp = gp;
                        c.onMove(event, gp.motionX, gp.motionY, gp.motionZ, gp.lastMotionX, gp.lastMotionY, gp.lastMotionZ, gp.deltaYaw, gp.deltaPitch, gp.lastDeltaYaw, gp.lastDeltaPitch);
                    }
                }
                gp.noCheckNextFlying = false;
                gp.lastHealth = p.getHealth();
            }
            gp.lastFallDistance = p.getFallDistance();
        }
    }

    @Override
    public void onPacketPlaySend(PacketPlaySendEvent event) {
        Player p = event.getPlayer();
        if(p != null) {
            GuardPlayerManager.addGuardPlayer(p);
            GuardPlayer gp = GuardPlayerManager.getGuardPlayer(p);
            if(gp != null) {
                if(event.getPacketId() == PacketType.Play.Server.KEEP_ALIVE) {

                    gp.serverKeepAlive = System.currentTimeMillis();
                }
                if(event.getPacketId() == PacketType.Play.Server.POSITION) {
                    WrappedPacketOutPosition ps = new WrappedPacketOutPosition(event.getNMSPacket());
                    final Vector teleportVector = new Vector(
                            ps.getPosition().getX(),
                            ps.getPosition().getY(),
                            ps.getPosition().getZ()
                    );
                    gp.teleports.add(teleportVector);
                }
                if(event.getPacketId() == PacketType.Play.Server.ENTITY_VELOCITY) {
                    WrappedPacketOutEntityVelocity velo = new WrappedPacketOutEntityVelocity(event.getNMSPacket());
                    if(velo.getEntityId() == p.getEntityId())
                        gp.lastVelocity = System.currentTimeMillis();
                }
                for (GuardCheck c : gp.getCheckManager().checks) {
                    c.gp = gp;
                    c.onPacketSend(event);
                }
            }
        }
    }

    private void handleBlocks(GuardPlayer gp) {
        final BoundingBox boundingBox = new BoundingBox(gp.getPlayer()).expandOther(0, 0, 0.55, 0.6, 0, 0);
        final double minX = boundingBox.getMinX();
        final double minY = boundingBox.getMinY();
        final double minZ = boundingBox.getMinZ();
        final double maxX = boundingBox.getMaxX();
        final double maxY = boundingBox.getMaxY();
        final double maxZ = boundingBox.getMaxZ();
        List<Block> b = new ArrayList<>();
        for (double x = minX; x <= maxX; x += (maxX - minX)) {
            for (double y = minY; y <= maxY + 0.01; y += (maxY - minY) / 5) {
                for (double z = minZ; z <= maxZ; z += (maxZ - minZ)) {
                    final Location location = new Location(gp.getPlayer().getWorld(), x, y, z);
                    final Block block = this.getBlock(location);
                    b.add(block);
                }
            }
        }
        final BoundingBox boundingBox2 = new BoundingBox(gp.getPlayer()).expandOther(0, 0, 0.01, 0, 0, 0);

        final double minX2 = boundingBox2.getMinX();
        final double minY2 = boundingBox2.getMinY();
        final double minZ2 = boundingBox2.getMinZ();
        final double maxX2 = boundingBox2.getMaxX();
        final double maxY2 = boundingBox2.getMaxY();
        final double maxZ2 = boundingBox2.getMaxZ();
        List<Block> b3 = new ArrayList<>();
        for (double x = minX2; x <= maxX2; x += (maxX2 - minX2)) {
            for (double y = minY2; y <= maxY2 + 0.01; y += (maxY2 - minY2) / 5) {
                for (double z = minZ2; z <= maxZ2; z += (maxZ2 - minZ2)) {
                    final Location location = new Location(gp.getPlayer().getWorld(), x, y, z);
                    final Block block = this.getBlock(location);
                    b3.add(block);
                }
            }
        }

        BoundingBox boundingBox3 = new BoundingBox(gp.getPlayer()).expandOther(0, 0, 0.01, -1.8, 0, 0);

         double minX3 = boundingBox3.getMinX();
         double minY3 = boundingBox3.getMinY();
         double minZ3 = boundingBox3.getMinZ();
         double maxX3 = boundingBox3.getMaxX();
         double maxY3 = boundingBox3.getMaxY();
         double maxZ3 = boundingBox3.getMaxZ();
        List<Block> b4 = new ArrayList<>();
        for (double x = minX3; x <= maxX3; x += (maxX3 - minX3)) {
            for (double y = minY3; y <= maxY3 + 0.01; y += (maxY3 - minY3) / 5) {
                for (double z = minZ3; z <= maxZ3; z += (maxZ3 - minZ3)) {
                    final Location location = new Location(gp.getPlayer().getWorld(), x, y, z);
                    final Block block = this.getBlock(location);
                    b4.add(block);
                }
            }
        }
        boundingBox3 = boundingBox3.expandOther(0, 0, 0.05, -0.01, 0, 0);
        minX3 = boundingBox3.getMinX();
        minY3 = boundingBox3.getMinY();
        minZ3 = boundingBox3.getMinZ();
        maxX3 = boundingBox3.getMaxX();
        maxY3 = boundingBox3.getMaxY();
        maxZ3 = boundingBox3.getMaxZ();
        List<Block> b5 = new ArrayList<>();
        for (double x = minX3; x <= maxX3; x += (maxX3 - minX3)) {
            for (double y = minY3; y <= maxY3 + 0.01; y += (maxY3 - minY3) / 5) {
                for (double z = minZ3; z <= maxZ3; z += (maxZ3 - minZ3)) {
                    final Location location = new Location(gp.getPlayer().getWorld(), x, y, z);
                    final Block block = this.getBlock(location);
                    b5.add(block);
                }
            }
        }



        gp.isInLiquid = b.stream().anyMatch(Block::isLiquid);
        gp.isInFullLiquid = b4.stream().allMatch(Block::isLiquid);
        gp.aboveLiquid = b5.stream().allMatch(Block::isLiquid);
        gp.inWeb = b3.stream().anyMatch(block -> block.getType().toString().contains("WEB"));
        gp.inAir = b.stream().allMatch(block -> block.getType() == Material.AIR);
        gp.onIce = b.stream().anyMatch(block -> block.getType().toString().contains("ICE"));
        if(gp.onIce) gp.lastIce = System.currentTimeMillis();
        gp.onSolidGround = b.stream().anyMatch(block -> block.getType().isSolid());
        gp.isOnSlab = b.stream().anyMatch(block -> block.getType().toString().contains("STEP") || block.getType().toString().contains("SLAB"));
        gp.isOnStair = b.stream().anyMatch(block -> block.getType().toString().contains("STAIR"));
        gp.nearTrapdoor = this.isCollidingAtLocation(gp,1.801, material -> material.toString().contains("TRAP_DOOR"));
        gp.blockAbove = b.stream().filter(block -> block.getLocation().getY() - gp.to.getY() > 1.5)
                .anyMatch(block -> block.getType() != Material.AIR) || gp.nearTrapdoor;
        if(gp.blockAbove) gp.lastBlockAbove = System.currentTimeMillis();
        gp.blockAboveWater = b.stream().filter(block -> block.getLocation().getY() - gp.to.getY() > 1.5)
                .allMatch(block -> block.isLiquid() || block.getType() == Material.AIR) || gp.nearTrapdoor;
        gp.onSlime = b.stream().anyMatch(block -> block.getType().toString().equalsIgnoreCase("SLIME_BLOCK"));
        gp.nearPiston = b.stream().anyMatch(block -> block.getType().toString().contains("PISTON"));
        gp.onLowBlock = b.stream().anyMatch(block -> block.getType().toString().contains("TRAP_DOOR") || block.getType().toString().contains("BED") || block.getType().toString().contains("CARPET") || block.getType().toString().contains("REPEATER") || block.getType().toString().contains("COMPARATOR") || block.getType().toString().contains("SLAB") || block.getType().toString().contains("SNOW") || block.getType().toString().contains("CAULDRON") || block.getType().toString().contains("BREWING") || block.getType().toString().contains("HOPPER") || block.getType().toString().contains("DETECTOR") || block.getType().toString().contains("ENCHANTING") || block.getType().toString().contains("END_PORTAL") || block.getType().toString().contains("POT") || block.getType().toString().contains("SOUL_SAND") || block.getType().toString().contains("STAIRS") || block.getType().toString().contains("SLAB") || block.getType().toString().contains("STAIR") || block.getType().toString().contains("STEP")  || block.getType().toString().contains("BED")  || block.getType().toString().contains("HEAD")  || block.getType().toString().contains("FENCE")  || block.getType().toString().contains("WALL")  || block.getType().toString().contains("PISTON")  || block.getType().toString().contains("SLIME"));
        if(gp.onLowBlock) gp.lastLowBlock = System.currentTimeMillis();
        final Location location = gp.getPlayer().getLocation();
        final int var1 = NumberConversions.floor(location.getX());
        final int var2 = NumberConversions.floor(location.getY());
        final int var3 = NumberConversions.floor(location.getZ());
        final Block var4 = this.getBlock(new Location(location.getWorld(), var1, var2, var3));

        BoundingBox box = new BoundingBox(var4);
        final BoundingBox bb = new BoundingBox(gp.getPlayer())
                .expandOther(0.2, 0.2, 0, 0, 0.2, 0.2);
        box.expand(Math.abs(gp.motionX) + 0.14, 0,
                Math.abs(gp.motionZ) + 0.14);
        final double mx = bb.getMinX();
        final double my = bb.getMinY();
        final double mz = bb.getMinZ();
        final double max = bb.getMaxX();
        final double may = bb.getMaxY();
        final double maz = bb.getMaxZ();
        List<Block> b2 = new ArrayList<>();
        for (double x = mx; x <= max; x += (max - mx)) {
            for (double y = my; y <= may + 0.01; y += (may - my) / 5) { //Expand max by 0.01 to compensate shortly for precision issues due to FP.
                for (double z = mz; z <= maz; z += (maz - mz)) {
                    final Location loc = new Location(gp.getPlayer().getWorld(), x, y, z);
                    final Block block = this.getBlock(loc);
                    b2.add(block);
                }
            }
        }
        if (!b2.stream().allMatch(block -> block.getType().toString().contains("AIR")))
            gp.collidesHorizontally = true;
        else
            gp.collidesHorizontally = false;
        gp.onClimbable = var4.getType() == Material.LADDER || var4.getType() == Material.VINE;
    }

    public boolean isCollidingAtLocation(GuardPlayer gp,double drop, Predicate<Material> predicate) {
        final ArrayList<Material> materials = new ArrayList<>();

        for (double x = -0.3; x <= 0.3; x += 0.3) {
            for (double z = -0.3; z <= 0.3; z+= 0.3) {
                final Material material = getBlock(gp.getPlayer().getLocation().clone().add(x, drop, z)).getType();
                if (material != null) {
                    materials.add(material);
                }
            }
        }

        return materials.stream().allMatch(predicate);
    }

    public int getMode(Collection<? extends Number> list) {
        int mode = (int) list.toArray()[0];
        int maxCount = 0;
        for (Number value : list) {
            int count = 1;
            for (Number value2 : list) {
                if (value2.equals(value))
                    count++;
                if (count > maxCount) {
                    mode = (int) value;
                    maxCount = count;
                }
            }
        }
        return mode;
    }

    //Taken from Fiona
    public Block getBlock(final Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return location.getBlock();
        } else {
            FutureTask<Block> futureTask = new FutureTask<>(() -> {
                location.getWorld().loadChunk(location.getBlockX() >> 4, location.getBlockZ() >> 4);
                return location.getBlock();
            });
            Bukkit.getScheduler().runTask(Guard.instance, futureTask);
            try {
                return futureTask.get();
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
            return null;
        }
    }

    public boolean sameLocation(GuardPlayer gp, WrappedPacketInFlying flying) {
        return gp.to.getX() == flying.getPosition().getX() && gp.to.getY() == flying.getPosition().getY() && gp.to.getZ() == flying.getPosition().getZ();
    }
}
