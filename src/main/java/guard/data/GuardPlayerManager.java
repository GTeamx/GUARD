package guard.data;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuardPlayerManager {

    public static List<GuardPlayer> guardPlayers = new ArrayList<>();

    public static void addGuardPlayer(Player player) {
        if(player == null) return;
        if(!doesGuardPlayerExist(player)) {
            guardPlayers.add(new GuardPlayer(player));
        }
    }
    public static void addGuardPlayerJoin(Player player) {
        if(player == null) return;
        if(!doesGuardPlayerExist(player)) {
            guardPlayers.add(new GuardPlayer(player));
        } else {
            guardPlayers.remove(getGuardPlayer(player));
            addGuardPlayer(player);
        }
    }
    public static void removeGuardPlayer(Player player) {
        if(player == null) return;
        if(!doesGuardPlayerExist(player)) return;
        for(int i = 0; i < guardPlayers.size(); i++) {
            GuardPlayer gp = guardPlayers.get(i);
            if(gp.getUuid() == player.getUniqueId()) {
                guardPlayers.remove(i);
            }
        }
    }

    public static GuardPlayer getGuardPlayer(Player player) {
        if(player == null) return null;
        for(int i = 0; i < guardPlayers.size(); i++) {
            GuardPlayer gp = guardPlayers.get(i);
            if(gp.getUuid() == player.getUniqueId()) {
                return gp;
            }
        }
        return null;
    }

    public static boolean doesGuardPlayerExist(Player player) {
        if(player == null) return true;
        for(int i = 0; i < guardPlayers.size(); i++) {
            GuardPlayer gp = guardPlayers.get(i);
            if(gp.getUuid() == player.getUniqueId()) {
                return true;
            }
        }
        return false;
    }

    public static void clearGuardPlayers() {
        guardPlayers.clear();
    }


}
