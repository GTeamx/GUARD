package guard.data;

// import com.sun.org.apache.xerces.internal.impl.xpath.XPath; -> java: package com.sun.org.apache.xerces.internal.impl.xpath does not exist

import guard.Guard;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.check.checks.movement.ground.GroundA;
import guard.check.checks.movement.invalid.InvalidA;
import guard.check.checks.movement.motion.MotionB;
import guard.check.checks.movement.motion.MotionC;
import guard.check.checks.movement.motion.MotionD;
import guard.check.checks.prediction.MotionA;
import guard.check.checks.prediction.SpeedA;
import guard.exempt.Exempt;
import guard.utils.*;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class PlayerData {

    public ArrayList<Check> checks = new ArrayList<>();
    private HashMap<String, HashMap<String, Integer>> flags = new HashMap<String, HashMap<String, Integer>>();
    public UUID uuid;
    public Player player;
    public String name;
    public boolean alertstoggled;
    public long lastTeleport;
    public long weirdTeleport;
    public Location from;
    public Location to;
    public boolean playerGround;
    public boolean lastplayerGround;
    public double motionX;
    public double motionY;
    public double motionZ;
    public double lastmotionX;
    public double lastmotionY;
    public double lastmotionZ;
    public float deltaYaw;
    public float deltaPitch;
    public float lastdeltaYaw;
    public float lastdeltaPitch;
    public long lastBlockplaced;
    public long lasthurt;
    public long lasthurtother;
    public Block blockplaced;
    public double lasthealth;
    public double lastFalldistance;
    public Location sto;
    public Location sfrom;
    public boolean aboveSlime;
    public boolean onLowBlock;
    public boolean isonStair;
    public boolean isInLiquid;
    public boolean isOnLadder;
    public boolean isonSlab;
    public boolean ground2;
    public boolean lastground2;
    public boolean blockabove;
    public boolean validVelocityHit;
    public boolean isDead;
    public long serverkeepalive;
    public long lastvelocity;
    public int ping;
    public long wasDead;
    public long tpafterded;
    public long tpafterjoin;
    public long joined;
    public long lastflyingtime;
    public int join;
    public int cancel;
    public Block brokenblock;
    public long lastelytraused;
    public long lastpiston;
    public boolean nearboat;
    public long lastnearboat;
    public boolean sentstartdestroy;
    public long brokeblock;
    public boolean pistonmove;
    public boolean inweb;
    public long lastice;
    public boolean onIce;
    public boolean onSlime;
    public long lastblockabove;
    public long lastslime;
    public Entity target;
    public Entity lasttargetreach;
    public double predymotion;
    public int airticks;
    public BoundingBox lastbox;
    public boolean inAir;
    public boolean onSolidGround;
    public boolean nearTrapdoor;
    public boolean nearPiston;
    public boolean isTeleporting;
    public boolean onClimbable;
    public int sinceSlimeTicks;
    public Exempt ex = new Exempt(this);
    public WrappedPacketInUseEntity.EntityUseAction useAction;
    public PastLocation targetpastlocations = new PastLocation();
    public Entity targetEntity;
    public ArrayList<Block> getBlocksaround = new ArrayList<>();
    public ArrayList<String> getMaterialsaround = new ArrayList<>();
    public SampleList<Double> mx = new SampleList<>(5, true);
    public SampleList<Double> my = new SampleList<>(5, true);
    public SampleList<Double> mz = new SampleList<>(5, true);




    public PlayerData(String name, UUID uuid) {
        this.uuid = uuid;
        this.name = name;
        player = Bukkit.getPlayer(uuid);
        lasthurt = System.currentTimeMillis();
        registerCheck(new GroundA());
        registerCheck(new SpeedA());
        registerCheck(new MotionA());
        registerCheck(new MotionB());
        registerCheck(new MotionC());
        registerCheck(new MotionD());
        registerCheck(new InvalidA());
        Bukkit.getScheduler().runTaskTimerAsynchronously(Guard.instance, ()-> {
            if(lasttargetreach != null) {
                targetpastlocations.addLocation(lasttargetreach.getLocation());
            }

        }, 0L, 1L);
    }

    public double getTPS() {
        return PacketEvents.get().getServerUtils().getTPS();
    }

    public void registerCheck(Check check) {
        CheckInfo info = check.getClass().getAnnotation(CheckInfo.class);

        check.name = info.name();
        check.category = info.category();
        check.enabled = Guard.instance.configUtils.getBooleanFromConfig("checks", info.name() + ".enabled");// config
        check.kickable = Guard.instance.configUtils.getBooleanFromConfig("checks", info.name() + ".Punishments.kick"); // config
        check.bannable = Guard.instance.configUtils.getBooleanFromConfig("checks", info.name() + ".Punishments.ban");// config
        if(Guard.instance.configUtils.getBooleanFromConfig("config", "silentchecks")) {// config decides
            check.silent = Guard.instance.configUtils.getBooleanFromConfig("checks", info.name() + ".silent");
        } else {
            check.silent = false;
        }
        check.maxBuffer = Guard.instance.configUtils.getDoubleFromConfig("checks", info.name() + ".Buffer.maxBuffer");// config
        check.addBuffer = Guard.instance.configUtils.getDoubleFromConfig("checks", info.name() + ".Buffer.addBuffer");; // config
        check.removeBuffer = Guard.instance.configUtils.getDoubleFromConfig("checks", info.name() + ".Buffer.removeBuffer");; // config
        if(!checks.contains(check))
            checks.add(check);
    }

    private void addCheck(Check check) {
        if(!checks.contains(check))
            this.checks.add(check);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getPlayer() {
        return player;
    }

    public int getDepthStriderLevel() {
        if (player.getInventory().getBoots() != null) {
            return player.getInventory().getBoots().getEnchantmentLevel(Enchantment.DEPTH_STRIDER);
        }
        return 0;
    }

    public Exempt getExempt() {
        return ex;
    }

    public void addFlag(String check) {
        HashMap<String, Integer> inner = flags.get(name);
        if(inner == null)
            inner = new HashMap<>();
        inner.put(check, inner.getOrDefault(check, 0) + 1);
        flags.put(name, inner);
    }

    public int getFlags(String plname, String type) {
        if (flags.get(plname) == null) return 0;
        return flags.get(plname).getOrDefault(type, 0);
    }


    public void flag(Check check, int treshold, Object... debug) {
        if(player != null) {
            addFlag(check.name);
            String buf = "";
            String Value = "";
            String Info = "";
            String Buffer = "";
            String maxBuffer = "";
            int i = 0;
            for (Object obj : debug) {
                i++;
                if(i == 1) {
                    Info = obj.toString();
                } else if(i == 2) {
                    Value = obj.toString();
                }else if(i == 3) {
                    Buffer = obj.toString();
                }else if(i == 4) {
                    maxBuffer = obj.toString();
                }
                buf += obj.toString() + ", ";
            }
            final String text = "§7Check: §3" + check.name + " \n§7Information: §3" + Info + " \n§7Value: §3" + Value + " \n§7Buffer: §3" + Buffer + "§7/" + maxBuffer;
            for (Player p : Bukkit.getOnlinePlayers()) {
                boolean d = Guard.instance.configUtils.getBooleanFromConfig("config", "testMode");
                PlayerData data = Data.data.getUserData(p);
                if ((data.alertstoggled && !d) || (d && !p.getName().equals(player.getName()))) { // TODO: /alerts command test later && (!d && !player.getName().equals(p.getName()))
                    TextComponent Flag = new TextComponent("§3§lGUARD §7»§f " + name + " §7failed §f" + check.name + " §7(§3x" + getFlags(name, check.name) + "§7)");
                    Flag.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder(text).create()));
                    p.spigot().sendMessage(Flag);
                }
            }
            if(Guard.instance.configUtils.getBooleanFromConfig("config", "testMode")) {
                TextComponent Flag = new TextComponent("§3§lGUARD §7»§f " + name + " §7failed §f" + check.name + " §7(§3x" + getFlags(name, check.name) + "§7)");
                Flag.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(text).create()));
                player.spigot().sendMessage(Flag);
            }
            if (getFlags(name, check.name) > treshold) {
                ban(name, check.bannable, check.kickable ,check.name
                        , Guard.instance.configUtils.getBooleanFromConfig("checks", check.name + ".Messages.broadcastPunish"));
            }
        }
    }

    public void ban(String r, boolean ban, boolean kick, String punishMessage, boolean broadcast) {
        //if(name.equalsIgnoreCase("Vagdedes2") || name.equalsIgnoreCase("XIII___")) {
        if(Guard.instance.configUtils.getBooleanFromConfig("config", "testMode")) {
            player.sendTitle("§cYou would be kicked by now.", "§3&lGUARD §7»§f " + punishMessage);
        } else {
            if(broadcast && ban || kick) {
                Bukkit.broadcastMessage(Guard.instance.configUtils.getConvertedStringFromConfig("checks", player, ".Messages.broadcastMessage"));
            }
            if(ban) {
                //Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), Guard.instance.configUtils.getConvertedStringFromConfig("checks", player, ".Messages.broadcastMessage"), );
                Bukkit.getScheduler().runTask(Guard.instance, () -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "ban " + player.getName() + " " + Guard.instance.configUtils.getConvertedStringFromConfig("checks", player, ".Messages.banMessage"));
                });
            } else {
                if(kick) {
                    Bukkit.getScheduler().runTask(Guard.instance, () -> {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kick " + player.getName() + " " + Guard.instance.configUtils.getConvertedStringFromConfig("checks", player, ".Messages.kickMessage"));
                    });
                    }
            }
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.isOp()) {
               // player.sendMessage("§c" + name + " §fhas been kicked for §c" + r);
            }
        }
        resetFlags(name);
    }



    public void setGetBlocksaround() {
        getBlocksaround.clear();
        getMaterialsaround.clear();
        if(player != null) {
           // BoundingBox boundingBox = new BoundingBox(to.getX(), to.getY(), to.getZ(), player.getWorld());
           // boundingBox.expand(0.5, 0.07, 0.5).move(0.0, -0.55, 0.0);
           // boolean touchingAir = boundingBox.checkBlocks(material -> material == Material.AIR);
           // isInLiquid = boundingBox.checkBlocks(material -> material == Material.WATER || material == Material.LAVA || material == Material.LEGACY_STATIONARY_WATER || material == Material.LEGACY_STATIONARY_LAVA);
            //isongroundshit()


        }
    }


    public boolean isOnGround() {
        if(player != null) {
            if (motionY == 0.015625) {
                return false;
            }

            if ((player.getLocation().getY() % 0.015625 < 0.0001)) {
                if (!onSolidGround) {
                    return false;
                }
                return true;
            }

            return player.getLocation().getY() % 0.015625 < 0.0001;
        }
        return false;
    }





    public int getPing(Player who) {
        try {
            String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".entity.CraftPlayer");
            Object handle = craftPlayer.getMethod("getHandle", new Class[0]).invoke(who, new Object[0]);
            return ((Integer)handle.getClass().getDeclaredField("ping").get(handle)).intValue();
        } catch (Exception e) {
            return 404;
        }
    }

    public void sendMessage(String Message) {
        if(player != null) {
            Bukkit.getScheduler().runTask(Guard.instance, () -> {
                player.sendMessage(Message);
            });
        }
    }

    public boolean ground2() {
        if(player != null) {
            if (motionY == 0.015625) {
                return false;
            }
            if ((player.getLocation().getY() % 0.015625 < 0.0001)) {
                if (!BlockUtils.onGroundshit(player)) {
                    return false;
                }
                return true;
            }

        return player.getLocation().getY() % 0.015625 < 0.0001;
        }
        return false;
    }
    public boolean isInLiquid() {
        if(player != null) {
            for(Block b : getBlocksaround) {


            }
        }
        return false;
    }






    public boolean isMaterial(Block m, String name) {
        //Bukkit.broadcastMessage("Materialstring=" + m.toString() + " Material=" + m);
        return m.toString().contains(name);
    }


    public boolean wasOnSlime(int max) {
        if(player != null) {
            for(Block b : getBlocksaround) {
                for (double i = 1.0D; i <= max; i++) {
                    if (b.getLocation().clone().subtract(0, i, 0).getBlock().toString().contains("SLIME")) return true;
                }

            }
        }
        return false;
    }

    public double getDeltaXZSqrt() {
        return Math.sqrt(motionX * motionX + motionZ * motionZ);
    }
    public double getLastDeltaXZSqrt() {
        return Math.sqrt(lastmotionX * lastmotionX + lastmotionZ * lastmotionZ);
    }

    public double getDeltaXZ() {
        return Math.hypot(motionX, motionZ);
    }
    public double getLastDeltaXZ() {
        return Math.hypot(lastmotionX, lastmotionZ);
    }




    public boolean blockabove() {
        if(player != null) {
          //  BoundingBox boundingBox = new BoundingBox(to.getX(), to.getY(), to.getZ(), player.getWorld());
          //  boundingBox.expand(0.3, 0.01, 0.3).move(0, 2.01, 0);
          //  return !boundingBox.checkBlocks(material -> material.toString().contains("AIR"));
            /**for(Block b : getBlocksaround) {
                    Block material3 = b.getLocation().clone().add(0, 2, 0).getBlock();
                    if ((!material3.toString().contains("AIR"))) {
                        lastblockabove = System.currentTimeMillis();
                        return true;
                    }

            } */
        }
        return false;
    }





    private long test;
    public void domove() {
        if(from == null) {
            from = to;
        }

        test = System.currentTimeMillis();
        lastmotionX = motionX;
        lastmotionY = motionY;
        lastmotionZ = motionZ;
        lastdeltaPitch = deltaPitch;
        lastdeltaYaw = deltaYaw;
        deltaPitch = to.getPitch() - from.getPitch();
        deltaYaw = yawTo180F(to.getYaw() - from.getYaw());
        motionX = to.getX() - from.getX();
        motionY = to.getY() - from.getY();
        motionZ = to.getZ() - from.getZ();
        mx.add(motionX);
        my.add(motionY);
        mz.add(motionZ);
       /** isonSlab = BlockUtils.isonSlab(player);
        aboveSlime = wasOnSlime(255);
        isonStair = BlockUtils.isonStair(player);
        isOnLadder = BlockUtils.isOnLadder(player);
        isInLiquid = BlockUtils.inLiquid(player);
        onLowBlock = BlockUtils.isonLowBlock(player);
        lastground2 = ground2;
        ground2 = ground2();
        blockabove = blockabove();
        pistonmove = BlockUtils.pistonmove(player);
        inweb = BlockUtils.inWeb(player);
        onIce = BlockUtils.onIce(player);
        onSlime = BlockUtils.onSlime(player); **/
        nearboat = false;
        predymotion = (lastmotionY - 0.08) * 0.9800000190734863;
        for (Entity e : getEntitiesAroundPoint(1.7)) {
            if(e instanceof Boat) {
               // data.sendMessage("Entity: " + e);
                nearboat = true;
                lastnearboat = System.currentTimeMillis();
            }
        }
        if(player != null) {
            if(player.isGliding()) {
                lastelytraused = System.currentTimeMillis();
            }
        }
        //sendMessage("took: " + (System.currentTimeMillis() - test) + " ms.");
        if(motionY > 0) {
           // sendMessage("my=" + motionY + " ground=" + ground2);
        }
        if(playerGround)
            airticks = 0;
        else
            airticks++;

    }

    public double getDistance(boolean y) {
        if(sfrom != null) {
            if (y) {
                Location newloc = sto.clone();
                newloc.setY(sfrom.clone().getY());
                return newloc.distance(sfrom.clone());
            }
            return sto.clone().distance(sfrom.clone()); // sto.distance(sfrom)
        }
        return 0;
    }

    public float yawTo180F(float flub) {
        if ((flub %= 360.0f) >= 180.0f) {
            flub -= 360.0f;
        }
        if (flub < -180.0f) {
            flub += 360.0f;
        }
        return flub;
    }

    public void resetFlags(String plname) {
        flags.put(plname, new HashMap<String, Integer>());
    }

    public List<Entity> getEntitiesAroundPoint(double radius) {
        List<Entity> entities = new ArrayList<Entity>();
        World world = to.getWorld();
        for (int x = (int) Math.floor((to.getX() - radius) / 16.0D); x <= Math.floor((to.getX() + radius) / 16.0D); x++) {
            for (int z = (int) Math.floor((to.getZ() - radius) / 16.0D); z <= Math.floor((to.getZ() + radius) / 16.0D); z++) {
                if (world.isChunkLoaded(x, z)) {
                    entities.addAll(Arrays.asList(world.getChunkAt(x, z).getEntities()));
                }
            }
        }
        Iterator<Entity> entityIterator = entities.iterator();
        while (entityIterator.hasNext()) {
            if (entityIterator.next().getLocation().distanceSquared(to) > radius * radius) {
                entityIterator.remove(); // Remove it
            }
        }
        return entities;
    }

    public double getgcd(double a, double b) {
        if(a < b) {
            return getgcd(b, a);
        }
        if(Math.abs(b) < 0.001) {
            return a;
        } else {
            return getgcd(b, a - Math.floor(a / b) * b);
        }
    }
    public double getMotionX(int ticks) {
        if(mx.size() > ticks - 1) {
            return mx.get(ticks - 1);
        }
        return 0;
    }
    public double getMotionY(int ticks) {
        if(my.size() > ticks - 1) {
            return my.get(ticks - 1);
        }
        return 0;
    }
    public double getMotionZ(int ticks) {
        if(mz.size() > ticks - 1) {
            return mz.get(ticks - 1);
        }
        return 0;
    }

    public double getGCDPitch() {
        return getgcd(Math.abs(deltaPitch), Math.abs(lastdeltaPitch));
    }
    public double getGCDYaw() {
        return getgcd(Math.abs(deltaYaw), Math.abs(lastdeltaYaw));
    }

    public float[] getRotations(Location one, Location two) {
        double diffX = two.getX() - one.getX();
        double diffZ = two.getZ() - one.getZ();
        double diffY = two.getY() + 2.0 - 0.4 - (one.getY() + 2.0);
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }
    public double[] getOffsetFromEntity(Player player, LivingEntity entity) {
        double yawOffset = Math.abs(yawTo180F(player.getEyeLocation().clone().getYaw()) - yawTo180F(getRotations(player.getLocation().clone(), entity.getLocation().clone())[0]));
        double pitchOffset = Math.abs(Math.abs(player.getEyeLocation().clone().getPitch()) - Math.abs(getRotations(player.getLocation().clone(), entity.getLocation().clone())[1]));
        return new double[]{yawOffset, pitchOffset};
    }

    public double getAngle() {
        Vector a = to.clone().toVector().subtract(from.clone().toVector()).normalize();
        Vector b = to.clone().getDirection();
        double angle = Math.acos(a.dot(b));
        return Math.toDegrees(angle);
    }



}