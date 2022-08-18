package guard.data;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketTracker;
import guard.Guard;
import guard.check.GuardCheck;
import guard.check.GuardCheckManager;
import guard.check.PredictionProcessor;
import guard.exempt.Exempt;
import guard.utils.BoundingBox;
import guard.utils.SampleList;
import guard.utils.packet.TransactionPacketServer;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import io.github.retrooper.packetevents.utils.versionlookup.viaversion.ViaVersionAccessor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

@Getter
@Setter
public class GuardPlayer {

    public Player player;
    public UUID uuid;
    public boolean alertsToggled;
    private HashMap<String, HashMap<String, Integer>> flags = new HashMap<String, HashMap<String, Integer>>();
    public Exempt exempt = new Exempt(this);
    public GuardCheckManager checkManager = new GuardCheckManager();
    public long serverKeepAlive;
    public ArrayDeque<Vector> teleports = new ArrayDeque<>();
    public Location from;
    public Location to;
    public boolean playerGround;
    public boolean serverGround;
    public long joined;
    public int join;
    public long tpAfterJoin;
    public int cancel;
    public long lastTeleport;
    public long weirdTeleport;
    public Block brokenBlock;
    public long lastBlockPlaced;
    public boolean lastPlayerGround;
    public boolean lastLastPlayerGround;
    public double motionX;
    public double motionY;
    public double motionZ;
    public double lastMotionX;
    public double lastMotionY;
    public double lastMotionZ;
    public float deltaYaw;
    public float deltaPitch;
    public float lastDeltaYaw;
    public float lastDeltaPitch;
    public double lastHealth;
    public double lastFallDistance;
    public boolean nearEntity;
    public Location sTo;
    public Location sFrom;
    public boolean isOnStair;
    public boolean isInLiquid;
    public boolean isInFullLiquid;
    public boolean aboveLiquid;
    public boolean isOnSlab;
    public boolean blockAbove;
    public boolean blockAboveWater;
    public double lastBlockAbove;
    public boolean isDead;
    public long lastVelocity;
    public int ping;
    public boolean inAnimation;
    public long wasDead;
    public long lastFlyingTime;
    public boolean sentStartDestroy;
    public long brokeBlock;
    public boolean inWeb;
    public boolean onIce;
    public double lastIce;
    public boolean onSlime;
    public double lastSlime;
    public boolean onLowBlock;
    public double lastLowBlock;
    public boolean collidesHorizontally;
    public Entity target;
    public Entity lastTarget;
    public WrappedPacketInUseEntity.EntityUseAction useAction;
    public boolean inAir;
    public boolean onSolidGround;
    public boolean nearTrapdoor;
    public boolean nearPiston;
    public boolean isTeleporting;
    public boolean onClimbable;
    public long entityHit;
    public long lastAttack;
    public long lastHurt;
    public long lastHurtOther;
    public boolean validVelocityHit;
    public int transactionPing;
    public SampleList<Integer> transactionPackets = new SampleList<>(10, true);
    public int kbLevel;
    public int sinceSlimeTicks;
    public Block blockPlaced;
    public boolean nearBoat;
    public long lastNearBoat;
    public int teleportTickFix;
    public int ticks;
    public boolean noCheckNextFlying;
    public boolean lastNoCheckNextFlying;
    public boolean lastLastNoCheckNextFlying;
    public double pMotionX;
    public double lastGlide;
    public double pMotionZ;
    public float lastAccelYaw;
    public float lastAccelPitch;
    public int cinematicTicks;
    public long lastCinematic;
    public boolean isCinematic;
    public ArrayDeque<Integer> sensitivitySamples = new ArrayDeque<>();
    public int sensitivity;
    public float realGCD;
    public int sentTransaction;
    public int transactionTick;
    public long sentTransactionTime;
    public List<TransactionPacketServer> transactions = new ArrayList<>();
    public PredictionProcessor predictionProcessor = new PredictionProcessor(this);
    public SampleList<Location> targetLocations = new SampleList<>(5, true);
    public PacketTracker packetTracker;

    public GuardPlayer(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        if(packetTracker == null) {
            if(Guard.instance.getServer().getPluginManager().getPlugin("Viaversion") != null) {
                UserConnection connection = Via.getManager().getConnectionManager().getConnectedClient(uuid);
                if(connection != null) {
                    packetTracker = connection.getPacketTracker();
                }
            }
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously(Guard.instance, () -> {
            if(target != null) {
                if(target != lastTarget) {
                    targetLocations.clear();
                }
                targetLocations.add(target.getLocation());
            }
        }, 1, 1);
        alertsToggled = Guard.instance.configUtils.getBooleanFromConfig("config", "testMode", false);
    }

    public void addFlag(String check) {
        HashMap<String, Integer> inner = flags.get(player.getName());
        if(inner == null)
            inner = new HashMap<>();
        inner.put(check, inner.getOrDefault(check, 0) + 1);
        flags.put(player.getName(), inner);
    }

    public int getFlags(String plname, String type) {
        if (flags.get(plname) == null) return 0;
        return flags.get(plname).getOrDefault(type, 0);
    }

    public void flag(GuardCheck check, int threshold, Object... debug) {
        if(player != null) {
            addFlag(check.name);
            String buf = "";
            String Value = "";
            String Info = "";
            String Buffer = "";
            String maxBuffer = "";
            String state = "";
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
                }else if(i == 5) {
                    state = obj.toString();
                }
                buf += obj.toString() + ", ";
            }
            final String text = "§7Check: §9" + check.name + " \n§7Information: §9" + Info + " \n§7Value: §9" + Value + " \n§7Buffer: §9" + Buffer + "§7/§9" + maxBuffer  + " \n§7State: §9" + state;
            final String prefix = Guard.instance.configUtils.getStringFromConfig("config", "prefix","§9§lGUARD §7»§f");
            for (Player p : Bukkit.getOnlinePlayers()) {
                boolean d = Guard.instance.configUtils.getBooleanFromConfig("config", "testMode", false);
                GuardPlayer gp = GuardPlayerManager.getGuardPlayer(p);
                if ((gp.alertsToggled)) { // TODO: /alerts command test later && (!d && !player.getName().equals(p.getName()))         || (d && !p.getName().equals(player.getName()))
                    TextComponent Flag = new TextComponent(prefix + " " + player.getName() + " §7failed §f" + check.name + " §7(§9x" + getFlags(player.getName(), check.name) + "§7)");
                    Flag.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder(text).create()));
                    p.spigot().sendMessage(Flag);
                }
            }
           /** if(Guard.instance.configUtils.getBooleanFromConfig("config", "testMode", false)) {
                TextComponent Flag = new TextComponent(prefix + " " + player.getName() + " §7failed §f" + check.name + " §7(§9x" + getFlags(player.getName(), check.name) + "§7)");
                Flag.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(text).create()));
                player.spigot().sendMessage(Flag);
            } **/
            if (getFlags(player.getName(), check.name) > threshold) {
                //ban(name, check.bannable, check.kickable ,check.name, Guard.instance.configUtils.getBooleanFromConfig("checks", check.name + ".Messages.broadcastPunish"), false);
            }
        }
    }

    public double getTPS() {
        return PacketEvents.get().getServerUtils().getTPS();
    }

    public boolean hasPotionEffect(PotionEffectType type) {
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if(potionEffect.getType().equals(type))
                return true;
        }
        return false;
    }
    public Optional<PotionEffect> getEffectByType(PotionEffectType type) {
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if(potionEffect.getType().equals(type))
                return Optional.of(potionEffect);
        }
        return Optional.empty();
    }
    public int getPotionEffectAmplifier(PotionEffectType type) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().getName().equals(type.getName())) {
                return (effect.getAmplifier() + 1);
            }
        }
        return 0;
    }

    public int getDepthStriderLevel() {
        if(PacketEvents.get().getServerUtils().getVersion().isNewerThanOrEquals(ServerVersion.v_1_8)) {
            if (player.getInventory().getBoots() != null) {
                return player.getInventory().getBoots().getEnchantmentLevel(Enchantment.DEPTH_STRIDER);
            }
        }
        return 0;
    }

    public float[] getPerfectHit(Player target) {
        final Vector eyesPos = new Vector(player.getLocation().clone().getX(), player.getLocation().clone().getY() + getEyeHeight(), player.getLocation().clone().getZ());
        final BoundingBox bb = new BoundingBox(target);
        final Vector vec = new Vector(bb.getMinX() + (bb.getMaxX() - bb.getMinX()) * 0.5, bb.getMinY() + (bb.getMaxY() - bb.getMinY()) * 0.5, bb.getMinZ() + (bb.getMaxZ() - bb.getMinZ()) * 0.5);
        final double diffX = vec.getX() - eyesPos.getX();
        final double diffY = vec.getY() - eyesPos.getY();
        final double diffZ = vec.getZ() - eyesPos.getZ();
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        pitch -= (float) (pitch % realGCD);
        yaw -= (float) (yaw % realGCD);
        return new float[] { wrapAngleTo180_float(yaw), wrapAngleTo180_float(pitch) };
    }

    public float getEyeHeight()
    {
        float f = 1.62F;

        if (player.isSleeping())
        {
            f = 0.2F;
        }

        if (player.isSneaking())
        {
            f -= 0.08F;
        }

        return f;
    }
    public float wrapAngleTo180_float(float value)
    {
        value = value % 360.0F;

        if (value >= 180.0F)
        {
            value -= 360.0F;
        }

        if (value < -180.0F)
        {
            value += 360.0F;
        }

        return value;
    }

    public void doMove() {
        if(from == null) {
            from = to;
        }

        lastMotionX = motionX;
        lastMotionY = motionY;
        lastMotionZ = motionZ;
        lastDeltaPitch = deltaPitch;
        lastDeltaYaw = deltaYaw;
        deltaPitch = to.getPitch() - from.getPitch();
        deltaYaw = yawTo180F(to.getYaw() - from.getYaw());
        motionX = to.getX() - from.getX();
        motionY = to.getY() - from.getY();
        motionZ = to.getZ() - from.getZ();
        nearBoat = getEntitiesInRadius(to.clone(), 1).stream().anyMatch(e -> e instanceof Boat);
        nearEntity = !getEntitiesInRadius(to.clone(), 1).isEmpty();
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

    public double getDeltaXZSqrt() {
        return Math.sqrt(motionX * motionX + motionZ * motionZ);
    }
    public double getLastDeltaXZSqrt() {
        return Math.sqrt(lastMotionX * lastMotionX + lastMotionZ * lastMotionZ);
    }

    public double getDeltaXZ() {
        return Math.hypot(motionX, motionZ);
    }
    public double getLastDeltaXZ() {
        return Math.hypot(lastMotionX, lastMotionZ);
    }
    public double getDistance(boolean y) {
        if (sFrom != null) {
            if (y) {
                Location newLocation = sTo.clone();
                newLocation.setY(sFrom.clone().getY());
                return newLocation.distance(sFrom.clone());
            }
            return sTo.clone().distance(sFrom.clone());
        }
        return 0;
    }

    public long getGcd(final long current, final long previous) {
        return (previous <= 16384L) ? current : getGcd(previous, current % previous);
    }

    public List<Entity> getEntitiesInRadius(Location location, double radius) {
        int maxX = (int) Math.floor((location.getX()+radius) / 16.0);
        int minX = (int) Math.floor((location.getX()-radius) / 16.0);
        int maxZ = (int) Math.floor((location.getZ()+radius) / 16.0);
        int minZ = (int) Math.floor((location.getZ()-radius) / 16.0);
        List<Entity> entities = new LinkedList<>();
        for(int i = minX; i <= maxX; i++) {
            for(int i2 = minZ; i2<= maxZ; i2++) {
                if(!location.getWorld().isChunkLoaded(i, i2)) continue;

                for(Entity entity : location.getWorld().getChunkAt(i, i2).getEntities()) {
                    if(entity == null) continue;
                    if(entity.getLocation().distanceSquared(location) > radius * radius) continue;
                    entities.add(entity);
                }
            }
        }
        return entities;
    }

    public double getGCD(final double v1, final double v2) {
        if(v1 < v2) {
            return getGCD(v2, v1);
        }
        if(Math.abs(v2) < 0.001) {
            return v1;
        } else {
            return getGCD(v2, v1 - Math.floor(v1 / v2) * v2);
        }
    }
}
