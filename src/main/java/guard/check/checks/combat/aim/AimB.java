package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.MathUtils;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Aim B", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class AimB extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        if(deltaPitch != 0) {

            double cPitch = deltaPitch * 16777216;
            double cLastPitch = lastDeltaPitch * 16777216;
            final double GCD = MathUtils.getGcd(cPitch, cLastPitch);
            final double moduloGCD = GCD % lastDeltaPitch;

            debug("moduloGCD=" + moduloGCD + " b=" + buffer);

            if(Math.abs(moduloGCD) > 20) fail(null, "GCD Flaw", "moduloGCD=" + moduloGCD);
            else removeBuffer();
        }

    }
}