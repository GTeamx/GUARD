package guard.check.checks.combat.aim.aimassist;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "AimAssist D", category = GuardCategory.Combat, state = GuardCheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0.5, maxBuffer = 1.5)
public class AimAssistD extends GuardCheck {

    double lastAccel;
    double lastAccelAccel;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        float absDeltaYaw = Math.abs(deltaYaw) - Math.abs(lastDeltaYaw);
        double accel = (Math.abs(absDeltaYaw));
        double accelAccel = Math.abs(lastAccel - (Math.abs(absDeltaYaw)));
        double accelAccelAccel = Math.abs(lastAccelAccel - accelAccel);
        debug("accelAccelAccel=" + accelAccelAccel + " % 5=" + (Math.floor(accelAccelAccel * 100)) % 2);
        if(String.valueOf(accelAccel).length() < 15 && accelAccel != 0) {
            debug("accelAccel=" + accelAccel);
            if(accelAccel == 3.0361328125 || String.valueOf(accelAccel).length() < 6) {
                fail(null, "Impossible rotation flaw", "accel §9" + accelAccel);
            }
        } else {
            removeBuffer();
        }
        lastAccelAccel = accelAccel;
        lastAccel = accel;
    }
}
