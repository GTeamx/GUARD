package guard.check.checks.movement.ground;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Ground A", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 1, maxBuffer = 1)
public class GroundA extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.NEAR_VEHICLE, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.CLIMBABLE, ExemptType.JOINED, ExemptType.STAIRS, ExemptType.SPECTATE, ExemptType.FLYING);
        debug("s=" + gp.serverGround + " c=" + gp.playerGround + " air=" + gp.inAir);
        if(!exempt && gp.serverGround != gp.playerGround) fail(packet, "Spoofed ground state", "s=" + gp.serverGround + " c=" + gp.playerGround);
        else removeBuffer();
    }
}
