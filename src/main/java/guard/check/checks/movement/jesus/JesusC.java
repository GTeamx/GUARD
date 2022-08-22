package guard.check.checks.movement.jesus;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@GuardCheckInfo(name = "Jesus C", category = GuardCategory.Movement, state = GuardCheckState.Coding, addBuffer = 1, removeBuffer = 0.01, maxBuffer = 1)
public class JesusC extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if(gp.aboveLiquid && !gp.isInFullLiquid) {
            boolean exempt = false;
            if(PacketEvents.get().getPlayerUtils().getClientVersion(gp.player).isNewerThanOrEquals(ClientVersion.v_1_13)) {
                if(gp.getPlayer().isSwimming()) {
                    exempt = true;
                }
            }
            if(motionY > 0.15 && !exempt) {
                fail(null, "Jumped Higher in Water", "motionY=" + motionY);
            }
        } else {
            removeBuffer();
        }
    }
}
