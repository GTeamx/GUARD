package guard.check.checks.movement.strafe;

import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Strafe A", category = Category.MOVEMENT)
public class StrafeA extends Check {

    double Angle;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {
        boolean exempt = isExempt(ExemptType.FLYING, ExemptType.INSIDE_VEHICLE);
        if(data.airticks > 3 && !exempt) {
            Angle = ((Math.pow((motionX + motionZ), 8)) - (Math.pow(data.getMotionX(3) + data.getMotionZ(3), 8)) * 0.91);
            if(Angle >= 0.000086) fail("Strafed in air", Angle);
        }
        if(Angle <= 0.000086) removeBuffer();
        //debug("ANGLE=" + Angle + " BUFFER=" + buffer + maxBuffer);
    }
}
