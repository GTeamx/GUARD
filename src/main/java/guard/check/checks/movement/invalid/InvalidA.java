package guard.check.checks.movement.invalid;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Invalid A", category = GuardCategory.Movement, state = GuardCheckState.Coding, addBuffer = 1, removeBuffer = 1, maxBuffer = 2)
public class InvalidA extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        double[] values = gp.predictionProcessor.predictUrAssOff();
        if(gp.getDeltaXZ() > values[0] && values[1] > 0.00005 && gp.getDeltaXZ() > 0) fail(packet, "Predictions unfollowed", "v=" + values[1]); else removeBuffer();
    }
}
