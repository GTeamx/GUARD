package guard.check.checks.movement.jesus;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Jesus A", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 1, maxBuffer = 1)
public class JesusA extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if(gp.aboveLiquid && !gp.isInFullLiquid && !isExempt(ExemptType.TELEPORT)) {
            fail(null, "Walked On Water", "true false");
            //debug("HAKE?");
        } else removeBuffer();
        debug("inLiquid=" + gp.isInLiquid + " aboveLiquid=" + gp.aboveLiquid + " inFullLiquid=" + gp.isInFullLiquid + " inAir=" + gp.inAir + " playerGround=" + gp.playerGround);
    }
}
