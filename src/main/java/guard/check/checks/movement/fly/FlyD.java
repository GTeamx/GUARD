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
        boolean exempt = isExempt(ExemptType.SLIME, ExemptType.SLAB, ExemptType.STAIRS, ExemptType.LIQUID, ExemptType.GLIDE, ExemptType.FLYING, ExemptType.NEAR_VEHICLE, ExemptType.INSIDE_VEHICLE, ExemptType.CLIMBABLE, ExemptType.TELEPORT, ExemptType.VELOCITY, ExemptType.FULL_LIQUID);
        motionPrediction = (lastMotionY - 0.08) * (double) 0.98F;
        if(gp.isInAir() && !gp.playerGround && !gp.onSolidGround && gp.inAir && !exempt && (motionY  - motionPrediction > 0.7)) fail(packet, "Predictions unfollowed", "lmy=" + motionY + " py=" + motionPrediction + " result=" + (motionY - motionPrediction));
        else if(gp.isInAir() && !gp.playerGround && !gp.onSolidGround && gp.inAir && !exempt && motionY < 0.05 && motionY > 0.0031) fail(packet, "Predictions unfollowed", "lmy=" + motionY + " py=" + motionPrediction + " result=" + (motionY    - motionPrediction));
        else removeBuffer();
    }
}
