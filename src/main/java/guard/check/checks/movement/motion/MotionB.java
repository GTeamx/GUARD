package guard.check.checks.movement.motion;

import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Motion B", category = Category.MOVEMENT)
public class MotionB extends Check {

    boolean wasinair = false;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {
        boolean exempt = isExempt(ExemptType.FLYING, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.CLIMBABLE, ExemptType.STAIRS, ExemptType.SLAB, ExemptType.LIQUID);
        if(!data.inAir) {
            wasinair = false;
        }else {
            if(!exempt) {
                if (motionY >= lastmotionY) {
                    if (wasinair) {
                        fail("Jumped in air", "Does not exist");
                    }
                    wasinair = true;
                } else {
                    removeBuffer();
                }
            }
        }


    }
}
