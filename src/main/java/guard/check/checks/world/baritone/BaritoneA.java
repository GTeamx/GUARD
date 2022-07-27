package guard.check.checks.world.baritone;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.Bukkit;

@GuardCheckInfo(name = "Baritone A", category = GuardCategory.World, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class BaritoneA extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        final String delta = String.valueOf(deltaPitch);
        if(deltaPitch < 0.0038 && deltaPitch > 0 && !delta.contains("E")) fail(packet, "Baritone head movements (A)", "p=" + deltaPitch + " lP=" + lastDeltaPitch);
        if(delta.contains("E") && deltaPitch > 0.001) fail(packet, "Baritone head movements (B)", "p=" + deltaPitch + " lP=" + lastDeltaPitch);
    }
}