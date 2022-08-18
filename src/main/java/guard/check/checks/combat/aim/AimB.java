package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.MathUtils;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Aim B", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0.15, maxBuffer = 2.25)
public class AimB extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        if(deltaPitch > 1) {

            final double gcdPitch = (MathUtils.getGcd(deltaPitch * MathUtils.EXPANDER, lastDeltaPitch * MathUtils.EXPANDER)) / MathUtils.EXPANDER;
            final double dfPitch = Math.abs((double) gp.getPlayer().getLocation().getPitch() % gcdPitch);

            debug("dfP=" + dfPitch + " b=" + buffer);

            if(dfPitch < 1.2E-5) fail(null, "GCD Flaw", "dfP=" + dfPitch);
            else removeBuffer();
        }

    }
}