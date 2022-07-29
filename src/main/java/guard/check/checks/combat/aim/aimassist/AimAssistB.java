package guard.check.checks.combat.aim.aimassist;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.MathUtils;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "AimAssist B", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class AimAssistB extends GuardCheck {
    double gcd = 0;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if (deltaYaw != 0 && deltaPitch != 0) {

            final long dp1 = (long) (deltaPitch * 16777216);
            final long dp2 = (long) (lastDeltaPitch * 16777216);
            gcd = MathUtils.getGcd(dp1, dp2);

            debug("GCD=" + gcd + " buffer=" + buffer);
            if (gcd < 400000 && gcd > 0) {
                if (++buffer > 6) {
                    fail(packet, "Incorrect GCD", "gcd=" + gcd);
                }
            } else {
                buffer = Math.max(0, buffer - 1);
            }
        }
    }

}
