package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Aim A", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 1, maxBuffer = 0)
public class AimA extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if (deltaPitch != 0 || deltaYaw != 0) {

            final float deltaY = deltaYaw % 360F;
            final float deltaP = deltaPitch;

            final boolean exempt = isExempt(ExemptType.TELEPORT);

            final boolean invalid = !exempt
                    && (deltaP % 1 == 0 || deltaY % 1 == 0) && deltaP != 0 && deltaY != 0;

            debug(String.format("teleport=%b, buffer=%.2f", exempt, buffer));

            if (invalid) {
                fail(packet, "Rounded Rotations", "dP=" + deltaP + " dY=" + deltaY);
            } else {
                buffer = Math.max(0, buffer - 0.25);
            }
        }
    }

}
