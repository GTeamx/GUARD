package guard.check.checks.movement.speed;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.graalvm.compiler.nodes.cfg.Block;

@GuardCheckInfo(name = "Speed A", category = GuardCategory.Movement, state = GuardCheckState.STABLE, addBuffer = 1, removeBuffer = 1, maxBuffer = 3)
public class SpeedA extends GuardCheck {

    long lastLiquid;
    double tempBuffer = 0;
    double tempBuffer2 = 0;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.FLYING, ExemptType.SLIME, ExemptType.VELOCITY, ExemptType.ICE);
        boolean exempt2 = isExempt(ExemptType.FLYING, ExemptType.SLIME, ExemptType.VELOCITY, ExemptType.TELEPORT, ExemptType.BLOCK_ABOVE, ExemptType.STAIRS, ExemptType.SLAB);
        long now = System.currentTimeMillis();
        double[] values = gp.predictionProcessor.predictUrAssOff();
        double[] values2 = gp.predictionProcessor.newPrediction();
        double threshold2 = (0.058 + (gp.playerGround ? 0.003 : 0) + (Math.abs(deltaYaw) > 1 ? 0.0055 : 0));
        double threshold = (gp.nearEntity ? 0.0001 : 0.000001);
        if(isExempt(ExemptType.VELOCITY)) threshold2 = 0.02;
        if(gp.isInLiquid) {

            lastLiquid = now;
        } else {
            if(now - lastLiquid < 100) {
                threshold += 0.002;
            }
        }
        if(gp.inWater || gp.inLava || gp.inClimbableBlock || gp.onClimbable || gp.inWeb) threshold2 = 0.001 + (gp.inClimbableBlock || gp.onClimbable ? 0.0009 : 0) + (gp.inWater || gp.inLava ? 0.003 : 0) + (gp.inWeb ? 0.003 : 0);
        threshold2 += (isExempt(ExemptType.ICE) ? 0.05 : 0);
        //debug("§cFLAG " + (gp.getDeltaXZ() >= values2[0]) + " " + (values2[1] > threshold2) + "" + (gp.getDeltaXZ() > 0));
        debug("diffXZ=" + values2[1] + "\npMotion=" + values2[0] + "\ndiffY=" + values2[2]);
        if((gp.getDeltaXZ() >= values2[0]) && (values2[1] > threshold2) && (gp.getDeltaXZ() > 0) && !exempt2 && Math.abs(deltaYaw) < 20) {
            tempBuffer++;
            if(tempBuffer > (gp.inWater || gp.inLava ? 6 :  3)) {
                tempBuffer2 = maxBuffer;
                maxBuffer = 0;
                fail(packet, "Generic movement speed modifications2", "speed §9" + values2[1]);

                maxBuffer = tempBuffer2;
            }
        } else {
            if(tempBuffer > 0) tempBuffer -= (gp.inWater || gp.inLava ? 0.1 : 0.5);
        }
        if(gp.getDeltaXZ() >= values[0] && values[1] > threshold && gp.getDeltaXZ() > 0 && !exempt) fail(packet, "Generic movement speed modifications", "speed §9" + values[1]); else removeBuffer();
    }
}
