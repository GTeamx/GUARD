package guard.runnable;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import guard.Guard;
import guard.data.GuardPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RunnableAuth implements Runnable {

    boolean doEnable;
    boolean wasFirst = true;


    @Override
    public void run() {
        /**if(!Guard.instance.auth.auth) {
            if(!wasFirst && !doEnable) {
                PacketEvents.getAPI().getEventManager().unregisterAllListeners();
                PacketEvents.getAPI().getInjector().uninject();
                GuardPlayerManager.clearGuardPlayers();
                wasFirst = true;
            }
            doEnable = true;
            if(!Guard.instance.configUtils.getStringFromConfig("config", "license", "").equals("")) {
                Guard.instance.auth.register(Guard.instance.configUtils.getStringFromConfig("config", "license", ""));
                if(Guard.instance.auth.auth) {
                    if (doEnable && wasFirst) {
                        PacketEvents.getAPI().init();
                        PacketEvents.getAPI().getEventManager().registerListener(Guard.instance.listener, PacketListenerPriority.HIGHEST);


                        PacketEvents.getAPI().getInjector().uninject();
                        PacketEvents.getAPI().getInjector().inject();
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            //PacketEvents.getAPI().getInjector().inject(p);
                        }
                        doEnable = false;
                        wasFirst = false;
                    }
                }
            }
        }else {
            if(doEnable && wasFirst) {
                PacketEvents.getAPI().init();
                PacketEvents.getAPI().getEventManager().registerListener(Guard.instance.listener, PacketListenerPriority.HIGHEST);
                PacketEvents.getAPI().getInjector().uninject();
                PacketEvents.getAPI().getInjector().inject();
                for(Player p : Bukkit.getOnlinePlayers()) {
                   // PacketEvents.get().getInjector().injectPlayer(p);
                }
                doEnable = false;
                //wasFirst = false;
            }
            if(!wasFirst) {
                Guard.instance.auth.login(Guard.instance.configUtils.getStringFromConfig("config", "license", "") + "1", Guard.instance.configUtils.getStringFromConfig("config", "license", "") + "2");
            }

        } **/

    }
}
