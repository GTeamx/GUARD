package guard.check.checks.combat.aim.aimassist;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.SampleList;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "AimAssist B", category = GuardCategory.Combat, state = GuardCheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0.2, maxBuffer = 4)
public class AimAssistB extends GuardCheck {

    SampleList<Float> yawSamples = new SampleList<>(10);
    SampleList<Float> pitchSamples = new SampleList<>(10);

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if(Math.abs(deltaPitch) > 0 && Math.abs(deltaYaw) > 0 ) {
            yawSamples.add(Math.abs(deltaYaw));
            pitchSamples.add(Math.abs(deltaPitch));
            if(yawSamples.isCollected() && pitchSamples.isCollected()) {
                double stdYaw = yawSamples.getStandardDeviation(yawSamples);
                double stdPitch = pitchSamples.getStandardDeviation(pitchSamples);
                double avgYaw = yawSamples.getAverageFloat(yawSamples);
                double avgPitch = pitchSamples.getAverageFloat(pitchSamples);
                if(stdPitch < 5 && stdYaw < 5 && avgYaw > 5.2 && avgPitch > 0.9 && avgPitch < 2.4) {
                    fail(null, "Static aiming pattern", "sP=" + stdPitch + " sY=" + stdYaw + " aP=" + avgPitch + " aY=" + avgYaw);
                    debug("§cFLAGER");
                } else removeBuffer();
                if(gp.isCinematic) buffer = 0;
                debug("sP=" + stdPitch + " sY=" + stdYaw + " aP=" + avgPitch + " aY=" + avgYaw);
            }
        }
    }
}
