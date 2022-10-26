package guard.check.checks.movement.ground;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.check.CheckState;
import guard.exempt.ExemptType;

@CheckInfo(name = "Ground A", category = Category.Movement, state = CheckState.STABLE)
public class GroundA extends Check {

    public void onMove(PacketReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        final boolean exempt = isExempt(ExemptType.NEAR_VEHICLE, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.STAIRS, ExemptType.SPECTATE, ExemptType.FLYING);

        if(!exempt && gp.serverGround != gp.playerGround) fail(packet, "Spoofed ground state", "s=§a" + gp.serverGround + "\n" + " §8»§f c=§a" + gp.playerGround);else removeBuffer();
    }

}
