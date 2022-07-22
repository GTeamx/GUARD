package guard.check.checks.movement.motion;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Motion A", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 1, maxBuffer = 2)
public class MotionA extends GuardCheck {
    double lastAccel;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {
        double predictedMotionY = (lastmotionY - 0.08D) * (double)0.98F;
        double diff = Math.abs(predictedMotionY - motionY);
        if(diff > 0.05 && gp.isInAir()) {
            fail(packet, "Player had irregular Y Motion", "move=" + motionY + " predicted=" + predictedMotionY);
        }else removeBuffer();
    }
}
