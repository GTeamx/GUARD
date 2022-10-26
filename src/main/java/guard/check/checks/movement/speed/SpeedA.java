package guard.check.checks.movement.speed;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.check.CheckState;
import guard.exempt.ExemptType;

@CheckInfo(name = "Speed A", category = Category.Movement, state = CheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0.5, maxBuffer = 1)
public class SpeedA extends Check {
    boolean isUsing;

    public void onMove(PacketReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        double[] values = gp.predictionProcessor.predictUrAssOff();
        boolean exempt = isExempt(ExemptType.STAIRS, ExemptType.SLAB, ExemptType.SLIME);
        double shiftedPred = values[1] * 10000;
        double xzMove = (gp.getDeltaXZ() - values[0]);
        //debug("" + xzMove + " " + shiftedPred);
        if(isUsing) {
            shiftedPred = shiftedPred / 100000;
            isUsing = false;
        }
        if(gp.getDeltaXZ() >= values[0] && shiftedPred > 0.00001 && gp.getDeltaXZ() > 0 && !exempt) {
            fail(null, "AH GOOFY ASS PREDICTION",xzMove + " " + shiftedPred);
            debug("" + xzMove + " " + shiftedPred);
        } else removeBuffer();
    }


    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketType() == PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT) {
            isUsing = true;
        }
    }
}
