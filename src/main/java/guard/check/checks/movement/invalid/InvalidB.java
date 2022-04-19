package guard.check.checks.movement.invalid;

import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Invalid B", category = Category.MOVEMENT)
public class InvalidB extends Check {

    double Speed;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {
        boolean exempt = isExempt(ExemptType.FLYING, ExemptType.INSIDE_VEHICLE, ExemptType.WEB, ExemptType.CLIMBABLE);
        Speed = Math.sqrt(Math.pow(Math.abs(motionX), 2) + Math.pow(Math.abs(motionZ), 2));
        Speed = Math.round(Speed * 10000000);
        if(!exempt && String.valueOf(Speed).contains("000")) fail("Constant Speed", "cs=" + Speed);
        if(!String.valueOf(Speed).contains("000")) removeBuffer();
    }
}
