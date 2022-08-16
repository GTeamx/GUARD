package guard.check.checks.movement.fly;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Fly B", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0, maxBuffer = 2)
public class FlyB extends GuardCheck {
    double motionPrediction = 999999999;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.SLIME, ExemptType.SLAB, ExemptType.STAIRS, ExemptType.LIQUID, ExemptType.GLIDE, ExemptType.FLYING, ExemptType.NEAR_VEHICLE, ExemptType.INSIDE_VEHICLE, ExemptType.CLIMBABLE, ExemptType.VELOCITY);
        motionPrediction = (lastMotionY - 0.08) * (double)0.98f;
        boolean isBedrock = PacketEvents.get().getPlayerUtils().isGeyserPlayer(gp.player) || gp.player.getName().contains(".");
        if(!gp.playerGround && !exempt && (motionPrediction - motionY > (isBedrock ? 0.005 : 0.000000000001))) fail(packet, "Predictions unfollowed", "lmy=" + motionY + " py=" + motionPrediction + " result=" + (motionPrediction - motionY));
        if(gp.playerGround) removeBuffer();
    }
}
