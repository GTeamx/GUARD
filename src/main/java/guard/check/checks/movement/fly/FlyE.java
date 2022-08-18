package guard.check.checks.movement.fly;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.potion.PotionEffectType;

@GuardCheckInfo(name = "Fly E", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0, maxBuffer = 1)
public class FlyE extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.FULL_LIQUID, ExemptType.CLIMBABLE, ExemptType.TELEPORT, ExemptType.GLIDE, ExemptType.NEAR_VEHICLE, ExemptType.PLACE);
        maxBuffer = isExempt(ExemptType.VELOCITY) ? 1 : 3;
        final double predictedMotionY = (lastMotionY - 0.08D) * (double)0.98F;
        if((motionY - predictedMotionY > 0.0000000001) && !gp.playerGround && !exempt) fail(packet, "Predictions unfollowed", "pred=" + predictedMotionY + " mY=" + motionY);
        else if(gp.playerGround && gp.serverGround) removeBuffer();
    }

}
