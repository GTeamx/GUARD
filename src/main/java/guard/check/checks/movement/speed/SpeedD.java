package guard.check.checks.movement.speed;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Speed D", category = GuardCategory.Movement, state = GuardCheckState.STABLE, addBuffer = 1, removeBuffer = 1, maxBuffer = 4)
public class SpeedD extends GuardCheck {
    double speed;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.FLYING, ExemptType.INSIDE_VEHICLE, ExemptType.WEB, ExemptType.CLIMBABLE, ExemptType.LIQUID, ExemptType.SOULSAND);
        speed = Math.sqrt(Math.pow(Math.abs(motionX), 2) + Math.pow(Math.abs(motionZ), 2));
        speed = Math.round(speed * 100000);
        if(!exempt && String.valueOf(speed).contains("000")) fail(packet, "Rounded horizontal movement", "speed §9" + speed);
        if(!String.valueOf(speed).contains("000")) removeBuffer();
    }

}
