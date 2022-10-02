package guard.check.checks.movement.speed;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
@GuardCheckInfo(name = "Speed G", category = GuardCategory.Movement, state = GuardCheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0.1, maxBuffer = 1)
public class SpeedG extends GuardCheck {

    double accelAccelSpeed;
    double accelAccelAccelSpeed;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        double accelSpeed = gp.getDeltaXZ()  - gp.getLastDeltaXZ() * 0.91;
        if(Math.abs(accelSpeed - accelAccelSpeed) > 0.0001 && Math.abs(accelSpeed - accelAccelSpeed) != 0)
            if(Math.abs(Math.abs(accelSpeed - accelAccelSpeed) - accelAccelAccelSpeed) < 0.00001 && !gp.playerGround) {
                fail(null, "Irregular Acceleration", Math.abs(Math.abs(accelSpeed - accelAccelSpeed) - accelAccelAccelSpeed) );
                debug("diffAccel=" + Math.abs(Math.abs(accelSpeed - accelAccelSpeed) - accelAccelAccelSpeed));
            } else removeBuffer();
        else removeBuffer();
        accelAccelAccelSpeed = Math.abs(accelSpeed - accelAccelSpeed);
        accelAccelSpeed = accelSpeed;
    }
}
