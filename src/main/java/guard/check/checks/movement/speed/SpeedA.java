package guard.check.checks.movement.speed;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.check.CheckState;
import guard.exempt.ExemptType;

@CheckInfo(name = "Speed A", category = Category.Movement, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 1.2, maxBuffer = 2)
public class SpeedA extends Check {
    long lastLiquid;

    public void onMove(PacketReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.FLYING, ExemptType.SLIME, ExemptType.VELOCITY, ExemptType.ICE);
        long now = System.currentTimeMillis();
        double[] values = gp.predictionProcessor.predictUrAssOff();
        double threshold = (gp.nearEntity ? 0.00001 : 0.000000000001);
        if(gp.isInLiquid) {

            lastLiquid = now;
        } else {
            if(now - lastLiquid < 100) {
                threshold += 0.002;
            }
        }
        //debug("§cFLAG " + (gp.getDeltaXZ() >= values2[0]) + " " + (values2[1] > threshold2) + "" + (gp.getDeltaXZ() > 0));
        if(gp.getDeltaXZ() >= values[0] && values[1] > threshold && gp.getDeltaXZ() > 0 && !exempt) fail(packet, "Generic movement speed modification", "speed §9" + values[1]); else removeBuffer();
    }
}
