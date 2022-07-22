package guard.check.checks.movement.invalid;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Invalid A", category = GuardCategory.Movement, state = GuardCheckState.Coding, addBuffer = 1, removeBuffer = 1, maxBuffer = 2)
public class InvalidA extends GuardCheck {


    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {
        double values[] = gp.predictionProcessor.predictUrAssOff();

        if(gp.getDeltaXZ() > values[0] && values[1] > 0.0000022 && gp.getDeltaXZ() > 0) fail(packet, "Did not follow Minecrafts Movements", values[1]); else removeBuffer();
    }
}
