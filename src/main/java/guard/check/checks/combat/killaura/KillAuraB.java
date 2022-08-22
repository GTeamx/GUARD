package guard.check.checks.combat.killaura;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "KillAura B", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 1, maxBuffer = 6)
public class KillAuraB extends GuardCheck {

    double lastDistance;

    @Override
    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        if(System.currentTimeMillis() - gp.lastAttack < 100L) {

            double distance = gp.getDistance(true);
            if(lastDistance != 0) {
                if(Math.abs(distance - lastDistance) < 0.001 && packet.getPlayer().isSprinting()) fail(packet, "Impossible Acceleration", "accel=" + Math.abs(distance - lastDistance));
                else removeBuffer();
            }
            lastDistance = distance;

        }

    }
}
