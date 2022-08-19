package guard.check.checks.movement.fly;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.potion.PotionEffectType;

    @GuardCheckInfo(name = "Fly E", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0, maxBuffer = 2)
public class FlyE extends GuardCheck {

    boolean wasVelocity;
    double tempBuffer = maxBuffer;
    boolean setBuffer;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.LIQUID, ExemptType.CLIMBABLE, ExemptType.TELEPORT, ExemptType.GLIDE, ExemptType.NEAR_VEHICLE, ExemptType.PLACE);
        if(tempBuffer != maxBuffer) {
            if(!setBuffer) {
                tempBuffer = maxBuffer;
                setBuffer = true;
            }
        }
        if(gp.playerGround) wasVelocity = false;
        if(isExempt(ExemptType.VELOCITY)) wasVelocity = true;
        final double predictedMotionY = (lastMotionY - 0.08D) * (double)0.98F;
        maxBuffer = (wasVelocity ? tempBuffer + 1 : tempBuffer);
        if((motionY - predictedMotionY > 0.0000000001) && !gp.playerGround && !exempt) fail(packet, "Predictions unfollowed", "pred=" + predictedMotionY + " mY=" + motionY);
        else if(gp.playerGround && gp.serverGround) removeBuffer();
    }

}
