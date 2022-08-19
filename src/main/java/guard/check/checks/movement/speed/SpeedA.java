package guard.check.checks.movement.speed;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Speed A", category = GuardCategory.Movement, state = GuardCheckState.Coding, addBuffer = 1, removeBuffer = 1, maxBuffer = 3)
public class SpeedA extends GuardCheck {

    long lastLiquid;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.FLYING, ExemptType.SLIME, ExemptType.VELOCITY);
        long now = System.currentTimeMillis();
        double[] values = gp.predictionProcessor.predictUrAssOff(); // 0.000000001 for fastmath lmao
        double threshold = (gp.nearEntity ? 0.0001 : 0.000001);
        if(gp.isInLiquid) {
            lastLiquid = now;
        } else {
            if(now - lastLiquid < 100) {
                threshold += 0.002;
            }
        }
        if(gp.getDeltaXZ() >= values[0] && values[1] > threshold && gp.getDeltaXZ() > 0 && !exempt) fail(packet, "Predictions unfollowed", "v=" + values[1]); else removeBuffer();
    }
}
