package guard.check.checks.movement.fly;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.utils.player.ClientVersion;

@GuardCheckInfo(name = "Fly D", category = GuardCategory.Movement, state = GuardCheckState.EXPERIMENTAL, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class FlyD extends GuardCheck {
    double motionPrediction = -999999999;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.SLIME, ExemptType.SLAB, ExemptType.STAIRS, ExemptType.LIQUID, ExemptType.GLIDE, ExemptType.FLYING, ExemptType.NEAR_VEHICLE, ExemptType.INSIDE_VEHICLE, ExemptType.CLIMBABLE, ExemptType.TELEPORT, ExemptType.VELOCITY, ExemptType.FULL_LIQUID, ExemptType.FLYING);
        motionPrediction = (lastMotionY - 0.08) * (double) 0.98F;
        if(!gp.playerGround && motionY < 0 && gp.inAir && !exempt) debug("r=" + Math.abs(motionY - motionPrediction) + " my=" + motionY + " d=" + (motionY - lastMotionY) + " fD=" + gp.getLastFallDistance());
        if(!gp.playerGround && PacketEvents.get().getPlayerUtils().getClientVersion(gp.player).isOlderThan(ClientVersion.v_1_13) && motionY < 0 && gp.inAir && !gp.onSolidGround && !gp.serverGround && !exempt && (Math.abs(motionY  - motionPrediction) > (gp.player.getFallDistance() > 2 ? 8.7E-13 : 8.7E-15)) && ((motionY - motionPrediction) > -0.08141626303492273)) fail(packet, "Downwards motion modifications (A)", "result §9" + Math.abs(motionY - motionPrediction));
        if((Math.abs(motionY - motionPrediction) != 0.07544406518948506) && Math.abs(motionY - motionPrediction) != (double) 0.02 && !exempt) {
            if (!gp.playerGround && PacketEvents.get().getPlayerUtils().getClientVersion(gp.player).isNewerThanOrEquals(ClientVersion.v_1_13) && motionY < 0 && gp.inAir && !gp.onSolidGround && !gp.serverGround && !exempt && (Math.abs(motionY - motionPrediction) > (gp.player.getFallDistance() > 2 ? 8.7E-13 : 9.72E-15))) fail(packet, "Downwards motion modifications (A)", "result §9" + Math.abs(motionY - motionPrediction));
            if (!gp.playerGround && gp.inAir && Math.abs(motionY) >= 0.01 && Math.abs(motionY) <= 0.1 && (Math.abs(motionY - motionPrediction) > 8.7E-15) && !isExempt(ExemptType.SLIME) && (Math.abs(motionY - motionPrediction) != 0.07544406518948506 || Math.abs(motionY - motionPrediction) != (double) 0.02)) fail(packet, "Downwards motion modifications (B)", "result §9" + Math.abs(motionY - motionPrediction));
        }
    }
}
