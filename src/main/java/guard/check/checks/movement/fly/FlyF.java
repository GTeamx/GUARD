package guard.check.checks.movement.fly;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Fly F", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0, maxBuffer = 2)
public class FlyF extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.FULL_LIQUID, ExemptType.CLIMBABLE, ExemptType.TELEPORT, ExemptType.GLIDE, ExemptType.NEAR_VEHICLE, ExemptType.SLIME, ExemptType.PLACE);
        maxBuffer = isExempt(ExemptType.VELOCITY) ? 2 : 4;
        final double predictedMotionY = (lastMotionY - 0.08D) * (double)0.98F;
        if((Math.abs(motionY - predictedMotionY) > 0.00000000001) && !gp.playerGround && !exempt) fail(packet, "Predictions unfollowed", "pred=" + predictedMotionY + " mY=" + motionY);
        else if(gp.playerGround && gp.serverGround) removeBuffer();
    }

}
