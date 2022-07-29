package guard.check.checks.combat.killaura;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "KillAura A", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 1, maxBuffer = 2)
public class KillAuraA extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        final double deltaXZ = gp.getDeltaXZ();
        final double lastDeltaXZ = gp.getLastDeltaXZ();

        final double acceleration = Math.abs(deltaXZ - lastDeltaXZ);
        if (gp.getTarget() == null) return;

        if (acceleration < 0.0025 && deltaXZ > 0.22 && System.currentTimeMillis() - gp.lastAttack < 250) {
            fail(packet, "KeepSprint KillAura", "accel=" + acceleration);
        } else {
            removeBuffer();
        }
    }
}
