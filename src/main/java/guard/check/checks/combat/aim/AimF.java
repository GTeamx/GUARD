package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.MathUtils;
import guard.utils.SampleList;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@GuardCheckInfo(name = "Aim F", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class AimF extends GuardCheck {
    final SampleList<Double> differenceSamples = new SampleList<>(25);

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if (deltaYaw != 0 && deltaPitch != 0) {

            if (gp.getTarget() != null) {
                final Location origin = gp.getPlayer().getLocation().clone();
                final Vector end = gp.getTarget().getLocation().clone().toVector();

                final float optimalYaw = origin.setDirection(end.subtract(origin.toVector())).getYaw() % 360F;
                final float rotationYaw = gp.getPlayer().getLocation().getYaw();
                final float fixedRotYaw = (rotationYaw % 360F + 360F) % 360F;

                final double difference = Math.abs(fixedRotYaw - optimalYaw);

                if (deltaYaw > 3f) {
                    differenceSamples.add(difference);
                }
                if (differenceSamples.isCollected()) {
                    final double average = MathUtils.getAverage(differenceSamples);
                    final double deviation = MathUtils.getStandardDeviation(differenceSamples);

                    final boolean invalid = average < 7 && deviation < 12;

                    if (invalid) {
                        if (++buffer > 0) {
                            fail(packet, "Rotations Flaw", "dv=" + deviation + " av=" + average);
                        }
                    } else {
                        buffer -= buffer > 0 ? 1 : 0;
                    }

                    debug("avg=" + average + " deviation=" + deviation + " buf=" + buffer);
                }
            }
        }
    }

}
