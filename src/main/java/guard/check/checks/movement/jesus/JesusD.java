package guard.check.checks.movement.jesus;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Jesus D", category = GuardCategory.Movement, state = GuardCheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 1, maxBuffer = 3)
public class JesusD extends GuardCheck {


    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if(gp.aboveLiquid || gp.isInFullLiquid) {
            if(motionY > 0.2 && gp.blockAboveWater && !isExempt(ExemptType.FLYING, ExemptType.SWIMMING)) {
                fail(null, "Impossible deltaY", "mY §9" + motionY);

            }else removeBuffer();
            debug("motionY=" + Math.abs(motionY));
        }
    }
}

