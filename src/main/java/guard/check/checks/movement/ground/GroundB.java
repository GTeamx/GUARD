package guard.check.checks.movement.ground;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.check.CheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.util.GeyserUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;

@CheckInfo(name = "Ground B", category = Category.Movement, state = CheckState.EXPERIMENTAL)
public class GroundB extends Check {

    double startY;

    public void onMove(PacketReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        // Check if player is on bedrock (aka exempt since fallDistance is modified on bedrock)

        final boolean isBedrock = GeyserUtil.isGeyserPlayer(gp.getPlayer().getUniqueId()) || gp.player.getName().contains(".");

        final boolean exempt = isExempt(ExemptType.NEAR_VEHICLE, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.CLIMBABLE, ExemptType.JOINED, ExemptType.STAIRS, ExemptType.VELOCITY, ExemptType.FLYING);

        final double predictedMotionY = (lastMotionY - 0.08D) * (double)0.98F;

        if(motionY >= 0 || !gp.inAir) {
            startY = gp.to.clone().getY();
        } else  {
            if(Math.abs(gp.player.getFallDistance() - Math.abs(gp.to.clone().getY() - startY)) - Math.abs(predictedMotionY) > 1.4 && !exempt && !isBedrock && !gp.onLowBlock) {
                fail(packet, "Modified fallDistance", "fallDistance §9" + (Math.abs(gp.player.getFallDistance() - Math.abs(gp.to.clone().getY() - startY)) - Math.abs(predictedMotionY)));
            }
        }
    }

}
