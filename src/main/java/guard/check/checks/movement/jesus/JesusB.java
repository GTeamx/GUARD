package guard.check.checks.movement.jesus;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.SampleList;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Jesus B", category = GuardCategory.Movement, state = GuardCheckState.Coding, addBuffer = 1, removeBuffer = 0.5, maxBuffer = 2)
public class JesusB extends GuardCheck {

    //SampleList<Double> patternMotionY = new SampleList<>(5);

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if(gp.aboveLiquid || gp.isInFullLiquid ) {
            //patternMotionY.add(Math.abs(motionY));
            //if(patternMotionY.isCollected()) {
               // if(patternMotionY.getAverageDouble(patternMotionY) > 0.1 && patternMotionY.getStandardDeviation(patternMotionY) < 0.09)
                    //debug("Pattern=" + patternMotionY.getAverageDouble(patternMotionY) + " std=" + patternMotionY.getStandardDeviation(patternMotionY));
            //}
            if(Math.abs(motionY) <= 0.0025 && Math.abs( motionY) >= 0 && gp.blockAboveWater) {
                fail(null, "Invalid DeltaY", "motionY=" + Math.abs(motionY));
            }else removeBuffer();

            //debug("motionY=" + Math.abs(motionY));
        }
    }
}
