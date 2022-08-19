package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.MathUtils;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Aim E", category = GuardCategory.Combat, state = GuardCheckState.Coding, addBuffer = 1, removeBuffer = 0.01, maxBuffer = 2)
public class AimE extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        if(deltaPitch != 0) {

            double cPitch = deltaPitch * MathUtils.EXPANDER;
            double cLastPitch = lastDeltaPitch * MathUtils.EXPANDER;
            final double GCD = MathUtils.getGcd(cPitch, cLastPitch);
            final double moduloGCD = Math.log(GCD % gp.getPlayer().getLocation().getPitch());

            debug("PITCH=" + moduloGCD);

            if(Math.abs(moduloGCD) > 20) fail(null, "GCD Flaw", "moduloGCD=" + moduloGCD);
            else removeBuffer();
        }

    }

}
