package guard.check.checks.combat.aim.aimassist;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "AimAssist C", category = GuardCategory.Combat, state = GuardCheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0.6, maxBuffer = 4.2)
public class AimAssistC extends GuardCheck {

    double lastAccel;
    double lastAccelAccel;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        float matherino = Math.abs(deltaYaw) - Math.abs(lastDeltaYaw);
        double accel = (Math.abs(matherino));
        double accelAccel = Math.abs(lastAccel - (Math.abs(matherino)));
        double accelAccelAccel = Math.abs(lastAccelAccel - accelAccel);
        debug("accelAccelAccel=" + accelAccelAccel + " % 5=" + (Math.floor(accelAccelAccel * 100)) % 2);
        if(String.valueOf(accelAccel).length() < 12 && accelAccel != 0) {
            debug("accelAccel=" + accelAccel);
            fail(null, "Acceleration flaw", "accel §9" + accelAccel);
        } else {
            removeBuffer();
        }
        lastAccelAccel = accelAccel;
        lastAccel = accel;
    }
}
