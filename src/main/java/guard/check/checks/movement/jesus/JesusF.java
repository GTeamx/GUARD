package guard.check.checks.movement.jesus;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.SampleList;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Jesus F", category = GuardCategory.Movement, state = GuardCheckState.Coding, addBuffer = 1, removeBuffer = .4, maxBuffer = 1)
public class JesusF extends GuardCheck {

    SampleList<Double> patternMotionY = new SampleList<>(4);

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if(gp.aboveLiquid || gp.isInFullLiquid ) {
            patternMotionY.add(Math.abs(motionY));
            if(patternMotionY.isCollected()) {
                if(patternMotionY.getAverageDouble(patternMotionY) >= 0.099 && patternMotionY.getAverageDouble(patternMotionY) < 0.2 && patternMotionY.getStandardDeviation(patternMotionY) < 0.09) {
                    //fail(null, "Invalid Pattern","avg=" + patternMotionY.getAverageDouble(patternMotionY) + " std=" + patternMotionY.getStandardDeviation(patternMotionY));
                }else if(patternMotionY.getAverageDouble(patternMotionY) >= 0 && patternMotionY.getAverageDouble(patternMotionY) < 0.009 && patternMotionY.getStandardDeviation(patternMotionY) < 0.09 && patternMotionY.getStandardDeviation(patternMotionY) > 0.001 || patternMotionY.getStandardDeviation(patternMotionY) == 0) {
                    //fail(null, "Invalid Pattern","avg=" + patternMotionY.getAverageDouble(patternMotionY) + " std=" + patternMotionY.getStandardDeviation(patternMotionY));
                }else removeBuffer();

            }


            //debug("motionY=" + Math.abs(motionY));
        }
    }
}
