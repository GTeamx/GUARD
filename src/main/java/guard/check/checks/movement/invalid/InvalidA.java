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
        boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.FLYING, ExemptType.INSIDE_VEHICLE);
        if(data.playerGround) InvalidA++;
        if(!data.playerGround) InvalidA = 0;
        if(InvalidA >= 8) MaxSpeed = 0.2864;
        if(InvalidA < 8) MaxSpeed = 0.6121838;
        if(isExempt(ExemptType.VELOCITY)) {
            InvalidA += data.kblevel;
            InvalidA += 0.45;
        }
        if(data.lastice <= 1200) InvalidA += 0.25;
        if(data.getDeltaXZ() >= MaxSpeed && !exempt) fail("Impossible Teleport", "cs=" + data.getDistance(true) + "ms=" + MaxSpeed);

    }
}

