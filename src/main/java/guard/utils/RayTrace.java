package guard.utils;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class RayTrace {

    Vector direction;
    Vector origin;

    // Gets the info from the Player
    public RayTrace(Player player) {
        this.direction = player.getEyeLocation().getDirection().clone();
        this.origin = player.getEyeLocation().toVector().clone();
    }

    // Here we get the EyeLocation Vector... int is for the different Options
    public double getOrigin(int i) {
        if(i == 0) {
            return origin.getX();
        }
        if(i == 1) {
            return origin.getY();
        }
        if(i == 2) {
            return origin.getZ();
        }
        return 0;
    }
    // Here we get the EyeLocation Direction... int is for the different Options
    public double getDirection(int i) {
        if(i == 0) {
            return direction.getX();
        }
        if(i == 1) {
            return direction.getY();
        }
        if(i == 2) {
            return direction.getZ();
        }
        return 0;
    }
}
