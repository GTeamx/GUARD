package guard.check.checks.movement.jesus;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import guard.utils.SampleList;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Jesus B", category = GuardCategory.Movement, state = GuardCheckState.Coding, addBuffer = 1, removeBuffer = 0.1, maxBuffer = 1.31)
public class JesusB extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        final double multiplier = gp.isInLiquid ? 0.8 : 0.5;
        final double accel = motionY - lastMotionY;

        final double prediction = (lastMotionY + 0.03999999910593033D) * multiplier - 0.02D;
        final double diff = Math.abs(motionY - prediction);

        debug("diff=" + diff);

        if(diff > 0.075 && motionY > 0.075 && accel >= 0.0 && gp.isInLiquid && !isExempt(ExemptType.TELEPORT)) fail(packet, "Predictions unfollowed", "pred=" + prediction + " mY=" + motionY);
        else removeBuffer();

    }
}
