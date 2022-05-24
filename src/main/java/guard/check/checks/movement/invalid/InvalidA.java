package guard.check.checks.movement.invalid;

import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Invalid A", category = Category.MOVEMENT)
public class InvalidA extends Check {

    int invalidA;
    double maxSpeed;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {
        boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.FLYING, ExemptType.INSIDE_VEHICLE);
        if(data.playerGround) invalidA++;
        if(!data.playerGround) invalidA = 0;
        if(invalidA >= 8) maxSpeed = 0.28804;
        if(invalidA < 8) maxSpeed = 0.6121838;
        if(isExempt(ExemptType.VELOCITY)) {
            invalidA += data.kblevel;
            invalidA += 0.45;
        }
        if(data.lastice <= 1200) invalidA += 0.25;
        if(data.getDeltaXZ() >= maxSpeed && !exempt) fail("Impossible Teleport", "cs=" + data.getDeltaXZ() + " ms=" + maxSpeed);

    }
}

