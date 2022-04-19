package guard.check.checks.movement.ground;

import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.exempt.ExemptType;
import guard.utils.TestBlockUtils;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Ground A", category = Category.MOVEMENT)
public class GroundA extends Check {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {
        boolean exempt = isExempt(ExemptType.NEAR_VEHICLE, ExemptType.FLYING, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.CLIMBABLE, ExemptType.JOINED, ExemptType.STAIRS);
        boolean mathground = data.to.getY() % 0.015625 == 0.0;
        debug("s=" + mathground + " c=" + data.playerGround);
        if(!exempt && mathground != data.playerGround) fail("Spoofed onGround Value", "s=" + mathground + " c=" + data.playerGround);
        else removeBuffer();

    }
}
