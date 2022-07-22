package guard.check.checks.movement.fly;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Fly C", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0, maxBuffer = 3)
public class FlyC extends GuardCheck {
    double motionPrediction = -999999999;
    double pastMotionY;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.SLIME, ExemptType.SLAB, ExemptType.STAIRS, ExemptType.LIQUID, ExemptType.GLIDE, ExemptType.FLYING, ExemptType.NEAR_VEHICLE, ExemptType.INSIDE_VEHICLE, ExemptType.CLIMBABLE);
        motionPrediction = (pastMotionY - 0.08) * 0.9800000190734863;
        pastMotionY = motionY;
        if(gp.isInAir() && !exempt && (pastMotionY - motionPrediction > 0.0000000000001)) fail(packet, "Predictions unfollowed", "lmy=" + pastMotionY + " py=" + motionPrediction + " result=" + (pastMotionY - motionPrediction));
        if(!gp.isInAir()) buffer = 0;
    }

}
