package guard.check.checks.movement.ground;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Ground C", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class GroundC extends GuardCheck {

    double startY;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        double predictedMotionY = (lastMotionY - 0.08D) * (double)0.98F;
        boolean exempt = isExempt(ExemptType.NEAR_VEHICLE, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.CLIMBABLE, ExemptType.JOINED, ExemptType.STAIRS, ExemptType.VELOCITY, ExemptType.FLYING);
        boolean isBedrock = PacketEvents.get().getPlayerUtils().isGeyserPlayer(gp.player) || gp.player.getName().contains(".");
        if(motionY >= 0 || !gp.inAir && !exempt) {
            startY = gp.to.clone().getY();
        } else  {
            if(Math.abs(gp.player.getFallDistance() - Math.abs(gp.to.clone().getY() - startY)) - Math.abs(predictedMotionY) > 1 && !exempt && !isBedrock) {
                fail(packet, "Spoofed FallDistance", Math.abs(gp.player.getFallDistance() - Math.abs(gp.to.clone().getY() - startY)) - Math.abs(predictedMotionY));
            }
        }
    }
}
