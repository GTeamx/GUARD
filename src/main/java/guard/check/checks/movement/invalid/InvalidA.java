package guard.check.checks.movement.invalid;

import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Invalid A", category = Category.MOVEMENT)
public class InvalidA extends Check {

    int InvalidA;
    double MaxSpeed;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {
        boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.JOINED);
        if(data.playerGround) InvalidA++;
        if(!data.playerGround) InvalidA = 0;
        if(InvalidA >= 8) MaxSpeed = 0.29;
        if(InvalidA < 8) MaxSpeed = 0.87;
        if(Math.abs(data.getMotionZ(1) + data.getMotionX(1)) >= MaxSpeed && !exempt) fail("Impossible Teleport", Math.abs(data.getMotionZ(1) + data.getMotionX(1)));
        if(Math.abs(data.getMotionZ(1) + data.getMotionX(1)) >= MaxSpeed && !exempt) fail("Teleported Horizontally", "cs=" + Math.abs(data.getMotionZ(1) + data.getMotionX(1)) + "mxs=" + MaxSpeed);

    }
}

