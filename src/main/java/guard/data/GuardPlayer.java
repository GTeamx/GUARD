package guard.data;

import guard.Guard;
import guard.check.GuardCheck;
import guard.check.GuardCheckManager;
import guard.check.PredictionProcessor;
import guard.exempt.Exempt;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

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
    public long joined;
    public int join;
    public long tpAfterJoin;
    public int cancel;
    public long lastTeleport;
    public long weirdTeleport;
    public Block brokenBlock;
    public long lastBlockPlaced;
    public boolean lastplayerGround;
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
    public Location sTo;
    public Location sFrom;
    public boolean isOnStair;
    public boolean isInLiquid;
    public boolean isOnSlab;
    public boolean blockAbove;
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
    public boolean onSlime;
    public boolean collidesHorizontally;
    public Entity target;
    public WrappedPacketInUseEntity.EntityUseAction useAction;
    public boolean inAir;
    public boolean onSolidGround;
    public boolean nearTrapdoor;
    public boolean nearPiston;
    public boolean isTeleporting;
    public boolean onClimbable;
    public long entityHit;
    public long lastHurt;
    public long lastHurtOther;
    public boolean validVelocityHit;
    public int kbLevel;
    public int sinceSlimeTicks;
    public Block blockPlaced;
    public boolean nearBoat;
    public long lastNearBoat;
    public int tpBandaidFixTicks;
    public int ticks;
    public boolean dontCheckNextFlying;
    public boolean lastDontCheckNextFlying;
    public boolean lastLastDontCheckNextFlying;
    public double pMotionX;
    public double pMotionZ;
    public PredictionProcessor predictionProcessor = new PredictionProcessor(this);

    public GuardPlayer(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
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
            final String text = "§7Check: §3" + check.name + " \n§7Information: §3" + Info + " \n§7Value: §3" + Value + " \n§7Buffer: §3" + Buffer + "§7/" + maxBuffer  + " \n§7State: §3" + state;
            final String prefix = Guard.instance.configUtils.getStringFromConfig("config", "prefix","§3§lGuard §7»§f");
            for (Player p : Bukkit.getOnlinePlayers()) {
                boolean d = Guard.instance.configUtils.getBooleanFromConfig("config", "testMode", false);
                GuardPlayer gp = GuardPlayerManager.getGuardPlayer(p);
                if ((gp.alertsToggled && !d) || (d && !p.getName().equals(player.getName()))) { // TODO: /alerts command test later && (!d && !player.getName().equals(p.getName()))
                    TextComponent Flag = new TextComponent(prefix + " " + player.getName() + " §7failed §f" + check.name + " §7(§3x" + getFlags(player.getName(), check.name) + "§7)");
                    Flag.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder(text).create()));
                    p.spigot().sendMessage(Flag);
                }
            }
            if(Guard.instance.configUtils.getBooleanFromConfig("config", "testMode", false)) {
                TextComponent Flag = new TextComponent(prefix + " " + player.getName() + " §7failed §f" + check.name + " §7(§3x" + getFlags(player.getName(), check.name) + "§7)");
                Flag.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(text).create()));
                player.spigot().sendMessage(Flag);
            }
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

    public int getDepthStriderLevel() {
        if(PacketEvents.get().getServerUtils().getVersion().isNewerThanOrEquals(ServerVersion.v_1_8)) {
            if (player.getInventory().getBoots() != null) {
                return player.getInventory().getBoots().getEnchantmentLevel(Enchantment.DEPTH_STRIDER);
            }
        }
        return 0;
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

}
