package guard.check.checks.world.baritone;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.Bukkit;

@GuardCheckInfo(name = "Baritone A", category = GuardCategory.World, state = GuardCheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class BaritoneA extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        //final String delta = String.valueOf(deltaPitch);
        //if(deltaPitch < 0.0038 && deltaPitch > 0 && !delta.contains("E")) fail(packet, "Baritone head movement (A)", "dP §9" + deltaPitch + "\n" + " §8»§f ldP §9" + lastDeltaPitch); else removeBuffer();
        //if(delta.contains("E") && deltaPitch > 0.0001) fail(packet, "Baritone head movement (B)", "dP §9" + deltaPitch + "\n" + " §8»§f ldP §9" + lastDeltaPitch); else removeBuffer();
        double diffX = gp.to.clone().getX() - Math.floor(gp.to.clone().getX());
        double diffZ = gp.to.clone().getZ() - Math.floor(gp.to.clone().getZ());
        debug("Hallo " + diffX + " " + diffZ);
        if((diffX > .490 && diffX < .510) || (diffZ > .490 && diffZ < .510)) {
            double yawDiff = Math.abs(deltaYaw) - Math.floor(Math.abs(deltaYaw));
            if(Math.abs(deltaYaw) > 0.1 && gp.getDeltaXZ() > 0 && yawDiff < 0.1 && Math.abs(deltaPitch) < 0.048 && Math.abs(deltaPitch) > 0) {
                Bukkit.broadcastMessage("§cBaritone is really cool");
            }
        }

    }
}