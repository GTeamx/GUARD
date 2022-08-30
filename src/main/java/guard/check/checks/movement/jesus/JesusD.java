package guard.check.checks.movement.jesus;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@GuardCheckInfo(name = "Jesus D", category = GuardCategory.Movement, state = GuardCheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0.25, maxBuffer = 0)
public class JesusD extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if (gp.isInFullLiquid && gp.blockAboveWater && !isExempt(ExemptType.FLYING, ExemptType.TELEPORT, ExemptType.SWIMMING)) {
            if (motionY - lastMotionY > 0 && motionY - lastMotionY < 0.0025) fail(null, "Repeated deltaY", "result §9" + (motionY - lastMotionY));
            else removeBuffer();
        }
    }
}
