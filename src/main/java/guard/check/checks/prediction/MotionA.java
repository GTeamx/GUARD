package guard.check.checks.prediction;

import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Motion A", category = Category.PREDICTION)
public class MotionA extends Check {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {
        boolean exempt = isExempt(ExemptType.FLYING, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.VELOCITY, ExemptType.INSIDE_VEHICLE, ExemptType.NEAR_VEHICLE, ExemptType.CLIMBABLE);
        double diff = Math.abs(data.motionY - data.predymotion);
        debug("dc=" + (diff > 0.001) + " a=" + (data.inAir) + " e=" +  (!exempt) + " d=" + diff);
        if(diff > 0.001 && data.inAir && !exempt) fail("MotionY Prediction", "d=" + diff); else removeBuffer();
    }
}
