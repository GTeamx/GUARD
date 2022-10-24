package guard.check.checks.movement.ground;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Ground B", category = GuardCategory.Movement, state = GuardCheckState.EXPERIMENTAL)
public class GroundB extends GuardCheck {

    double startY;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        // Check if player is on bedrock (aka exempt since fallDistance is modified on bedrock)
        final boolean isBedrock = PacketEvents.get().getPlayerUtils().isGeyserPlayer(gp.player) || gp.player.getName().contains(".");

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
