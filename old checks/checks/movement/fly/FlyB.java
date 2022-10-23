package guard.check.checks.movement.fly;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Fly B", category = GuardCategory.Movement, state = GuardCheckState.STABLE, addBuffer = 1, removeBuffer = 0, maxBuffer = 4)
public class FlyB extends GuardCheck {
    double motionPrediction = Double.MAX_VALUE;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.SLIME, ExemptType.SLAB, ExemptType.STAIRS, ExemptType.FULL_LIQUID, ExemptType.GLIDE, ExemptType.FLYING, ExemptType.NEAR_VEHICLE, ExemptType.INSIDE_VEHICLE, ExemptType.CLIMBABLE, ExemptType.VELOCITY, ExemptType.PLACE, ExemptType.TELEPORT, ExemptType.LIQUID, ExemptType.TRAPDOOR);
        motionPrediction = (lastMotionY - 0.08) * (double)0.98f;
        boolean isBedrock = PacketEvents.get().getPlayerUtils().isGeyserPlayer(gp.player) || gp.player.getName().contains(".");
        debug("result=" + (motionPrediction - motionY) + "/" + 0.000000000001 +  " pred=" + motionPrediction + " mY=" + motionY);
        if(!gp.onCake && !gp.playerGround && !exempt && (motionPrediction - motionY > (isBedrock ? 0.005 : 0.000000000001))) fail(packet, "Generic gravity modifications", "mY §9" + motionY + "\n" + " §8»§f predicted §9" + motionPrediction + "\n" + " §8»§f result §9" + (motionPrediction - motionY) + "/" + 0.000000000001);
        if(gp.playerGround && gp.serverGround) removeBuffer();
    }
}
