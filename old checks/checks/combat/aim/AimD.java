package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import guard.utils.MathUtils;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Aim D", category = GuardCategory.Combat, state = GuardCheckState.STABLE, addBuffer = 1, removeBuffer = 0.01, maxBuffer = 3)
public class AimD extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        if(deltaPitch != 0) {

            double cPitch = deltaPitch * 16777216;
            double cLastPitch = lastDeltaPitch * 16777216;
            final double GCD = MathUtils.getGcd(cPitch, cLastPitch);
            final double moduloGCD = GCD % gp.getPlayer().getLocation().getPitch();

            debug("moduloGCD=" + moduloGCD);

            if(String.valueOf(moduloGCD).length() <= 3 && !isExempt(ExemptType.TELEPORT)) fail(packet, "Rounded pitch rotation", "mGCD §9" + moduloGCD);
            else removeBuffer();
        }
    }
}
