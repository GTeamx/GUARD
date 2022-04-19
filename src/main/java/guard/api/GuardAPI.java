package guard.api;

import guard.api.check.CheckManager;
import guard.api.check.GuardCheck;
import guard.check.Check;
import guard.data.Data;
import guard.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuardAPI {
    public static CheckManager cm = new CheckManager();

    public static PlayerData getPlayerData(Player p) {
        if(p != null) {
            Data.data.registerUser(p);
            return  Data.data.getUserData(p);
        }
        return null;
    }

    public static void addCheck(GuardCheck check) {

        cm.addCheck(check);
    }

    public static void removeCheck(GuardCheck check) {
        //if(!checksremove.contains(check))
        //checksremove.add(check);
        //getPlayerData(check.data.getPlayer()).removeapicheck(check);
        // }
        cm.removeCheck(check);
    }


}
