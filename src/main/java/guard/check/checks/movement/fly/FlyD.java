package guard.check.checks.movement.fly;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Fly D", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class FlyD extends GuardCheck {
    double motionPrediction = -999999999;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        double predictedMotionY = (lastMotionY - 0.08D) * (double)0.98F;
        double diff = Math.abs(predictedMotionY - motionY);
        if(diff > 0.0000000000004 && gp.isInAir() && motionY < 0 && motionY != (((-0 - 0.08D) * (double)0.98F)) && motionY != -0.07840000152587834 && !gp.playerGround) {
            fail(packet, "Predictions unfollowed", "move=" + motionY + " predicted=" + predictedMotionY);
        }else removeBuffer();
    }
}
