package guard;

import guard.command.Command;
import guard.data.GuardPlayerManager;
import guard.license.Auth;
import guard.listener.Event;
import guard.listener.PacketListener;
import guard.utils.ConfigUtils;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.settings.PacketEventsSettings;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class Guard extends JavaPlugin {

    public static Guard instance;
    public PacketListener listener;
    public ConfigUtils configUtils;
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
        PacketEvents.get().loadAsyncNewThread();
    }
    @Override

    public void onEnable() {
        instance = this;
        listener = new PacketListener();
        GuardPlayerManager.clearGuardPlayers();
        Bukkit.getPluginManager().registerEvents(new Event(), this);
        Bukkit.getConsoleSender().sendMessage("§aGuard is now Enabled!");
        Bukkit.getPluginCommand("guard").setExecutor(new Command());
        configUtils = new ConfigUtils(this);
       /** try {
            auth.printCurrentWorkingDirectory1();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        auth.init();
        auth.register("KZ0YK7-HAFNX1-HK0E4L-J2HN3W-I8DLWJ-THO129-HVQJ4P");

        if(auth.auth) {
            System.out.println("noice");
        } **/
        //Bukkit.getScheduler().runTaskTimerAsynchronously(this, new RunnableAuth(), 0, 20*60);
        PacketEvents.get().init();
        PacketEvents.get().registerListener(Guard.instance.listener);


        PacketEvents.get().getInjector().eject();
        PacketEvents.get().getInjector().inject();
        for (Player p : Bukkit.getOnlinePlayers()) {
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
