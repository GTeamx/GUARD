package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Aim B", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0.25, maxBuffer = 2)
public class AimB extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if (deltaPitch != 0 || deltaYaw != 0) {
            final double yawAccel = Math.abs(deltaYaw - lastDeltaYaw);
            final double pitchAccel = Math.abs(deltaPitch - lastDeltaPitch);

            final boolean exempt = isExempt(ExemptType.TELEPORT);
            final boolean invalidYaw = yawAccel < 0.1 && Math.abs(deltaYaw) > 1.5F;
            final boolean invalidPitch = pitchAccel < 0.1 && Math.abs(deltaPitch) > 1.5F;

            final String info = String.format(
                    "dY=%.2f, dP=%.2f, yA=%.2f, pA=%.2f",
                    deltaYaw, deltaPitch, yawAccel, pitchAccel
            );

            debug(info);

            if ((invalidPitch || invalidYaw) && !exempt) {
                fail(packet, "Constant Rotations", "yA=" + yawAccel + " pA=" + pitchAccel);
            } else {
                removeBuffer();
            }
        }
    }

}
