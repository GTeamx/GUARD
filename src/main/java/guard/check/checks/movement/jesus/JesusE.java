package guard.check.checks.movement.jesus;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Jesus E", category = GuardCategory.Movement, state = GuardCheckState.Coding, addBuffer = 1, removeBuffer = 1, maxBuffer = 2)
public class JesusE extends GuardCheck {


    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if(gp.aboveLiquid || gp.isInFullLiquid ) {
            if(motionY > 0.2 && gp.blockAboveWater) {
                fail(null, "Invalid DeltaY", "motionY=" + motionY);

            }else removeBuffer();
            debug("motionY=" + Math.abs(motionY));
        }
    }
}

