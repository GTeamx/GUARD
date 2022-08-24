package guard.check.checks.movement.jesus;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@GuardCheckInfo(name = "Jesus D", category = GuardCategory.Movement, state = GuardCheckState.Coding, addBuffer = 1, removeBuffer = 1, maxBuffer = 2)
public class JesusD extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = false;
        if(PacketEvents.get().getPlayerUtils().getClientVersion(gp.player).isNewerThanOrEquals(ClientVersion.v_1_13)) {
            if(gp.getPlayer().isSwimming()) {
                exempt = true;
            }
        }
        if(gp.isInFullLiquid && gp.blockAboveWater && !isExempt(ExemptType.FLYING, ExemptType.TELEPORT)) {
            if(motionY - lastMotionY <= 0.0025 && motionY - lastMotionY >= 0 && motionY < 0.09 && motionY > -0.1 && !exempt && !String.valueOf(motionY).startsWith("0.03999999") && !String.valueOf(motionY).startsWith("0.040000000")) {
                fail(null, "Same DeltaY", "motionY=" + motionY);
                debug("" +( motionY - lastMotionY));
            }
        } else removeBuffer();
    }
}
