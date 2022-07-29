package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Aim G", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class AimG extends GuardCheck {
    private float lastDy, lastLastDeltaYaw;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if (deltaYaw != 0 && deltaPitch != 0) {

            if (deltaYaw < 5F && lastDy > 20F && lastLastDeltaYaw < 5F) {
                final double low = (deltaYaw + lastLastDeltaYaw) / 2;
                final double high = lastDy;

                fail(packet, "Snappy Rotations", "low=" + low + " high=" + high);
            }

            lastLastDeltaYaw = lastDy;
            lastDy = deltaYaw;
        }
    }

}
