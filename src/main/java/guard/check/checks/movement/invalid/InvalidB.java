package guard.check.checks.movement.invalid;

import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Invalid B", category = Category.MOVEMENT)
public class InvalidB extends Check {

    double speed;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {
        boolean exempt = isExempt(ExemptType.FLYING, ExemptType.INSIDE_VEHICLE, ExemptType.WEB, ExemptType.CLIMBABLE);
        speed = Math.sqrt(Math.pow(Math.abs(motionX), 2) + Math.pow(Math.abs(motionZ), 2));
        speed = Math.round(speed * 10000000);
        if(!exempt && String.valueOf(speed).contains("000")) fail("Constant Speed", "cs=" + speed);
        if(!String.valueOf(speed).contains("000")) removeBuffer();
    }
}
