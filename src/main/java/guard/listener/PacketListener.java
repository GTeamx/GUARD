package guard.listener;

import guard.Guard;
import guard.check.GuardCheck;
import guard.data.GuardPlayer;
import guard.data.GuardPlayerManager;
import guard.utils.BoundingBox;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.PacketListenerAbstract;
import io.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.custompayload.WrappedPacketInCustomPayload;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.packetwrappers.play.out.entityvelocity.WrappedPacketOutEntityVelocity;
import io.github.retrooper.packetevents.packetwrappers.play.out.position.WrappedPacketOutPosition;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.function.Predicate;

public class PacketListener extends PacketListenerAbstract {
    private final List<Block> blocks = new ArrayList<>();

    public PacketListener() {
        super(PacketListenerPriority.HIGH);
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        Player p = event.getPlayer();

        GuardPlayerManager.addGuardPlayer(p);
        GuardPlayer gp = GuardPlayerManager.getGuardPlayer(p);
        NMSPacket packet = event.getNMSPacket();
        // Bukkit.broadcastMessage("s");
        if(gp != null) {
            //if (data.join++ > 50) {

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
            }
            gp.predictionProcessor.handle(event);
            for (GuardCheck c : gp.getCheckManager().checks) {
                c.gp = gp;
                c.onPacket(event);
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
                    gp.from = gp.to;
                    gp.to = from;
                    gp.sFrom = gp.sTo;
                    gp.sTo = from;
                    gp.doMove();
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
                        } catch (ConcurrentModificationException ignored) {

                        }
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

                // }
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
        blocks.clear();
        final BoundingBox boundingBox = new BoundingBox(gp.getPlayer())
                .expandSpecific(0, 0, 0.55, 0.6, 0, 0);

        final double minX = boundingBox.getMinX();
        final double minY = boundingBox.getMinY();
        final double minZ = boundingBox.getMinZ();
        final double maxX = boundingBox.getMaxX();
        final double maxY = boundingBox.getMaxY();
        final double maxZ = boundingBox.getMaxZ();
        List<Block> b = new ArrayList<>();
        for (double x = minX; x <= maxX; x += (maxX - minX)) {
            for (double y = minY; y <= maxY + 0.01; y += (maxY - minY) / 5) { //Expand max by 0.01 to compensate shortly for precision issues due to FP.
                for (double z = minZ; z <= maxZ; z += (maxZ - minZ)) {
                    final Location location = new Location(gp.getPlayer().getWorld(), x, y, z);
                    final Block block = this.getBlock(location);
                    blocks.add(block);
                    b.add(block);
                }
            }
        }

        gp.isInLiquid = b.stream().anyMatch(Block::isLiquid);
        gp.inWeb = b.stream().anyMatch(block -> block.getType().toString().contains("WEB"));
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
        gp.onSlime = b.stream().anyMatch(block -> block.getType().toString().equalsIgnoreCase("SLIME_BLOCK"));
        if(gp.onSlime) System.currentTimeMillis();
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
                .expandSpecific(0.2, 0.2, 0, 0, 0.2, 0.2);
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
        //data.sendMessage("" + data.collidesHorizontally + " " + var4.getType() + " " + data.isonStair);
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
