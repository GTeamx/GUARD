package guard.check.checks.prediction;

import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;

@CheckInfo(name = "Speed A", category = Category.PREDICTION)
public class SpeedA extends Check {

    double prediction;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {
        WrappedPacketInFlying flying = new WrappedPacketInFlying(packet.getNMSPacket());
        if(flying.isMoving()) {
            prediction = data.getLastDeltaXZ() * (double) 0.91F + (data.player.isSprinting() ? 0.026 : 0.02);
            double diff = data.getDeltaXZ() - prediction;
            boolean exempt = isExempt(ExemptType.NEAR_VEHICLE, ExemptType.FLYING, ExemptType.TELEPORT, ExemptType.VELOCITY);
            debug("d=" + diff);
            if(data.airticks > 2 && !exempt) {
                if(diff > 0.006) {
                    fail("Speed Prediction", "d=" + diff);
                } else {
                    removeBuffer();
                }
            }
        }
    }
}
