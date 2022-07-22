package guard.runnable;

import guard.Guard;
import guard.data.GuardPlayerManager;
import io.github.retrooper.packetevents.PacketEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RunnableAuth implements Runnable {

    boolean doEnable;
    boolean wasFirst = true;


    @Override
    public void run() {
        if(!Guard.instance.auth.auth) {
            if(!wasFirst && !doEnable) {
                PacketEvents.get().unregisterListener(Guard.instance.listener);
                PacketEvents.get().getInjector().eject();
                GuardPlayerManager.clearGuardPlayers();
                wasFirst = true;
            }
            doEnable = true;
            if(!Guard.instance.configUtils.getStringFromConfig("config", "license", "").equals(""))
                Guard.instance.auth.register(Guard.instance.configUtils.getStringFromConfig("config", "license", ""));
            if(doEnable && wasFirst) {
                PacketEvents.get().init();
                PacketEvents.get().registerListener(Guard.instance.listener);


                PacketEvents.get().getInjector().eject();
                PacketEvents.get().getInjector().inject();
                for(Player p : Bukkit.getOnlinePlayers()) {
                    PacketEvents.get().getInjector().injectPlayer(p);
                }
                doEnable = false;
                wasFirst = false;
            }
        }else {
            if(doEnable && wasFirst) {
                PacketEvents.get().init();
                PacketEvents.get().registerListener(Guard.instance.listener);


                PacketEvents.get().getInjector().eject();
                PacketEvents.get().getInjector().inject();
                for(Player p : Bukkit.getOnlinePlayers()) {
                    PacketEvents.get().getInjector().injectPlayer(p);
                }
                doEnable = false;
                wasFirst = false;
            }
            if(!wasFirst) {
                Guard.instance.auth.login(Guard.instance.configUtils.getStringFromConfig("config", "license", "") + "1", Guard.instance.configUtils.getStringFromConfig("config", "license", "") + "2");
            }

        }

    }
}
