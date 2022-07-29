package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Aim D", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class AimD extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        final double deltaXz = gp.getDeltaXZ();
        final double lastDeltaXz = gp.getLastDeltaXZ();
        final double accelXz = Math.abs(deltaXz - lastDeltaXz);

        debug("accel=" + accelXz + " dY=" + deltaYaw);

        if (deltaYaw > 10 && accelXz < 0.00001 && deltaXz != 0) {
            fail(packet, "Impossible Acceleration", "acc=" + accelXz);
        }
    }

}
