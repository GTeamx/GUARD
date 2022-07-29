package guard.check.checks.combat.aim.aimassist;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.MathUtils;
import guard.utils.SampleList;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "AimAssist C", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class AimAssistC extends GuardCheck {
    private final SampleList<Float> yawAccelSamples = new SampleList<>(20);
    private final SampleList<Float> pitchAccelSamples = new SampleList<>(20);

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if (deltaYaw != 0 && deltaPitch != 0) {
            // Exempt Cinematic
            final float yawAccel = (deltaYaw - lastDeltaYaw);
            final float pitchAccel = (deltaPitch - lastDeltaPitch);

            yawAccelSamples.add(yawAccel);
            pitchAccelSamples.add(pitchAccel);

            if (yawAccelSamples.isCollected() && pitchAccelSamples.isCollected()) {
                final double yawAccelAverage = MathUtils.getAverage(yawAccelSamples);
                final double pitchAccelAverage = MathUtils.getAverage(pitchAccelSamples);

                final double yawAccelDeviation = MathUtils.getStandardDeviation(yawAccelSamples);
                final double pitchAccelDeviation = MathUtils.getStandardDeviation(pitchAccelSamples);

                final boolean exemptRotation = deltaYaw < 1.5F;

                final boolean averageInvalid = yawAccelAverage < 1 || pitchAccelAverage < 1 && !exemptRotation;
                final boolean deviationInvalid = yawAccelDeviation < 5 && pitchAccelDeviation > 5 && !exemptRotation;

                debug(String.format(
                        "yaa=%.2f, paa=%.2f, yad=%.2f, pad=%.2f, buf=%.2f",
                        yawAccelAverage, pitchAccelAverage, yawAccelDeviation, pitchAccelDeviation, buffer
                ));

                if (averageInvalid && deviationInvalid) {
                    buffer = Math.min(buffer + 1, 20);
                    if (buffer > 0) {
                        fail(packet, "Impossible Rotations", "aI=" + averageInvalid + " dI=" + deviationInvalid);
                    }
                } else {
                    buffer -= buffer > 0 ? 0.75 : 0;
                }
            }
        }
    }

}
