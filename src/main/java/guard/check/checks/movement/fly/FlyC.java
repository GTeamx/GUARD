package guard.check.checks.movement.fly;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Fly C", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0, maxBuffer = 3)
public class FlyC extends GuardCheck {
    double motionPrediction = -999999999;
    double maxValue = 0.0000000000001;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.SLIME, ExemptType.SLAB, ExemptType.STAIRS, ExemptType.FULL_LIQUID, ExemptType.GLIDE, ExemptType.FLYING, ExemptType.NEAR_VEHICLE, ExemptType.INSIDE_VEHICLE, ExemptType.CLIMBABLE);
        motionPrediction = (lastMotionY - 0.08) * (double)0.98f;
        maxValue = 0.0000000000001;
        //if(isExempt(ExemptType.PLACE) && motionY > 0) maxValue = 0.64;
        boolean isBedrock = PacketEvents.get().getPlayerUtils().isGeyserPlayer(gp.player) || gp.player.getName().contains(".");
        if(!gp.playerGround && !exempt && (motionY  - motionPrediction > (isBedrock ? 0.0001 : maxValue))) fail(packet, "Predictions unfollowed", "lmy=" + motionY + " py=" + motionPrediction + " result=" + (motionY    - motionPrediction) + "/" + maxValue);
        if(gp.playerGround && gp.serverGround) removeBuffer();
    }

}
