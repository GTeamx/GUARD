package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.MathUtils;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Aim B", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0.05, maxBuffer = 2)
public class AimB extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        if(deltaPitch > 1) {

            final double expendedPitch = deltaPitch * MathUtils.EXPANDER;
            final double lastExpendedPitch = lastDeltaPitch * MathUtils.EXPANDER;

            final double gcdPitch = MathUtils.getGcd(expendedPitch, lastExpendedPitch);
            final double dividedPitch = gcdPitch / MathUtils.EXPANDER;

            final double dfPitch = Math.abs((double) gp.getPlayer().getLocation().getPitch() % dividedPitch);

            debug("dfP=" + dfPitch + " b=" + buffer);

            if(dfPitch < 0.00008) fail(null, "GCD Flaw", "dfP=" + dfPitch);
            else removeBuffer();
        }

    }
}