package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.MathUtils;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Aim A", category = GuardCategory.Combat, state = GuardCheckState.STABLE, addBuffer = 1, removeBuffer = 0, maxBuffer = 9)
public class AimA extends GuardCheck {

    public double customGCD(float current, float previous) {
        if(current < previous) return customGCD(previous, current);
        if(Math.abs(previous) < 0.001) return current;
        if(Math.abs(previous) > 0.001) return customGCD(previous, (float) (current - Math.floor(current / previous) * previous));
        return 0;
    }

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        if(deltaPitch != 0) {
            final double GCD = customGCD(Math.abs(gp.deltaPitch), Math.abs(gp.lastDeltaPitch));
            if(GCD < 0.07 && GCD != 0 && !gp.isCinematic) fail(packet, "Did not follow proper GCD", "gcd §9" + GCD);
        }

    }
}