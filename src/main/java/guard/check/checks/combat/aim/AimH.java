package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Aim H", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class AimH extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if (deltaPitch != 0 || deltaYaw != 0) {

            debug("tp=" + isExempt(ExemptType.TELEPORT));

            if (deltaPitch == 0 && deltaYaw == 0 && !isExempt(ExemptType.TELEPORT)) fail(packet, "Impossible Rotations", "dP=" + deltaPitch + " dY=" + deltaYaw);
        }
    }

}
