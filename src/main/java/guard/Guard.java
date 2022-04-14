package guard;

import guard.command.Command;
import guard.data.Data;
import guard.listener.Event;
import guard.listener.PacketListener;
import guard.utils.ConfigUtils;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.settings.PacketEventsSettings;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Guard extends JavaPlugin {

    public static Guard instance;
    public PacketListener listener;
    public ConfigUtils configUtils;
    @Override
    public void onLoad() {
        PacketEvents.create(this);
        PacketEventsSettings settings = PacketEvents.get().getSettings();
        settings
                .fallbackServerVersion(ServerVersion.v_1_7_10)
                .compatInjector(false)
                .checkForUpdates(false);
        PacketEvents.get().load();
        PacketEvents.get().loadAsyncNewThread();
    }
    @Override

    public void onEnable() {
        instance = this;
        listener = new PacketListener();
        Data.data.clearDataBase();
        Bukkit.getPluginManager().registerEvents(new Event(), this);
        Bukkit.getConsoleSender().sendMessage("§aGuard is now Enabled!");
        Bukkit.getPluginCommand("guard").setExecutor(new Command());
        configUtils = new ConfigUtils(this);
        PacketEvents.get().init();
        PacketEvents.get().registerListener(listener);

        PacketEvents.get().getInjector().eject();
        PacketEvents.get().getInjector().inject();
        for(Player p : Bukkit.getOnlinePlayers()) {
            PacketEvents.get().getInjector().injectPlayer(p);
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§cGuard is now Disabled!");
        PacketEvents.get().terminate();
        Bukkit.getScheduler().cancelTasks(this);

    }

}
