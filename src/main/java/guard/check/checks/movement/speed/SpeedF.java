package guard.check.checks.movement.speed;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Speed F", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0.20, maxBuffer = 1)
public class SpeedF extends GuardCheck {

    @Override
    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.CLIMBABLE, ExemptType.STAIRS, ExemptType.SLAB);
        final double predictedMotion = Math.sqrt(Math.abs(motionY - lastMotionY));
        if(!gp.onLowBlock && !exempt && (motionY - predictedMotion < -0.6456 || (motionY - predictedMotion > 0 && motionY - predictedMotion != 0.0386)) && !gp.inAir && gp.onSolidGround) fail(packet, "YPort Motion"," res=" + (motionY - predictedMotion));
        else removeBuffer();
        if(!gp.inAir && motionY > -0.38 && motionY <= 0.42) debug("res=" + (motionY - predictedMotion) + " my=" + motionY + " g=" + gp.playerGround + " sG=" + gp.onSolidGround + " sG=" + gp.serverGround + " iA=" + gp.inAir);

    }
}
