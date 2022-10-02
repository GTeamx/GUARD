package guard;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.ViaBackwardsConfig;
import guard.command.Command;
import guard.data.GuardPlayerManager;
import guard.license.AES;
import guard.license.Auth;
import guard.listener.ClientBrandListener;
import guard.listener.Event;
import guard.listener.PacketListener;
import guard.utils.ConfigUtils;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.settings.PacketEventsSettings;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.UUID;

public class Guard extends JavaPlugin {

    public static Guard instance;
    public ConfigUtils configUtils;
    public PacketListener listener;
    public Auth auth = new Auth(("G" + "u" + "a" + "r" + "d" + " " + "A" + "n" + "t" + "i" + "-" + "C" + "h" + "e" + "a" + "t"), ("x" + "8" + "Q" + "G" + "p" + "h" + "q" + "k" + "c" + "h"), "12a7150c1688bf2b86c549c966c6c68cc33411d8accc397bcad1ca26e525a33e", ("1" + "." + "0"), ("h" + "t" + "t" + "p" + "s" + ":" + "/" + "/" + "k" + "e" + "y" + "a" + "u" + "t" + "h" + "." + "w" + "i" + "n" + "/" + "a" + "p" + "i" + "/" + "1" + "." + "1" + "/"));;


    @Override
    public void onLoad() {
        PacketEvents.create(this);
        PacketEventsSettings settings = PacketEvents.get().getSettings();
        settings
                .fallbackServerVersion(ServerVersion.v_1_7_10)
                .compatInjector(false)
                .checkForUpdates(false);
        PacketEvents.get().load();
       // PacketEvents.get().loadAsyncNewThread();
    }

    @Override
    public void onEnable() {
        instance = this;
        listener = new PacketListener();
        GuardPlayerManager.clearGuardPlayers();
        Bukkit.getPluginManager().registerEvents(new Event(), this);
        Bukkit.getPluginManager().registerEvents(new ClientBrandListener(), this);
        Bukkit.getConsoleSender().sendMessage("§aGuard is now Enabled!");
        Bukkit.getPluginCommand("guard").setExecutor(new Command());
        configUtils = new ConfigUtils(this);
        PacketEvents.get().init();
        PacketEvents.get().registerListener(Guard.instance.listener);

        // FOR TESTING AUTH ENCRYPTION
        System.out.println("OUTPUT: " + AES.AESEncrypt2("1.0", "12a7150c1688bf2b86c549c966c6c68cc33411d8accc397bcad1ca26e525a33e", "643134ed859db780df3d505ac459ef06d52c470b2979ec7ab2588dd67e5817e7"));


        PacketEvents.get().getInjector().eject();
        PacketEvents.get().getInjector().inject();
        Bukkit.getScheduler().runTaskLater(this, () -> {

        }, 20);
        if(PacketEvents.get().getServerUtils().getVersion().isNewerThanOrEquals(ServerVersion.v_1_13)) {
            final Messenger messenger = Bukkit.getMessenger();
            messenger.registerIncomingPluginChannel(this, "minecraft:brand", new ClientBrandListener());
        } else {
            final Messenger messenger = Bukkit.getMessenger();
            messenger.registerIncomingPluginChannel(this, "MC|BRAND", new ClientBrandListener());
        }
        if(getServer().getPluginManager().getPlugin("ViaBackwards") != null) {
            if(getServer().getPluginManager().getPlugin("ViaBackwards").isEnabled()) {
                Class<?> ViaBackwardsClass = ViaBackwardsConfig.class;
                try {
                    ViaBackwardsConfig config = (ViaBackwardsConfig) ViaBackwards.getConfig();
                    Field handlePingsAsInvAcknowledgements = ViaBackwardsClass.getDeclaredField("handlePingsAsInvAcknowledgements");
                    handlePingsAsInvAcknowledgements.setAccessible(true);
                    handlePingsAsInvAcknowledgements.setBoolean(config, true);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        for(Player player : Bukkit.getOnlinePlayers()) {
            GuardPlayerManager.addGuardPlayer(player);
            final String prefix = Guard.instance.configUtils.getStringFromConfig("config", "prefix","§9§lGUARD §7»§f");
            player.sendMessage(prefix + " REJOIN PLS");
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§cGuard is now Disabled!");
        //PacketEvents.get().getInjector().eject();
        /**for(Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            InetSocketAddress address = player.getAddress();
            PacketEvents.get().getPlayerUtils().loginTime.remove(uuid);
            PacketEvents.get().getPlayerUtils().playerPingMap.remove(uuid);
            PacketEvents.get().getPlayerUtils().playerSmoothedPingMap.remove(uuid);
            PacketEvents.get().getPlayerUtils().clientVersionsMap.remove(address);
            PacketEvents.get().getPlayerUtils().tempClientVersionMap.remove(address);
            PacketEvents.get().getPlayerUtils().keepAliveMap.remove(uuid);
            PacketEvents.get().getPlayerUtils().channels.remove(player.getName());
            PacketEvents.get().getServerUtils().entityCache.remove(player.getEntityId());
        } */
        PacketEvents.get().terminate();
        Bukkit.getScheduler().cancelTasks(this);

    }
}
