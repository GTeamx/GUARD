package guard.check.checks.movement.step;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.check.CheckState;
import guard.exempt.ExemptType;

@CheckInfo(name = "Step A", category = Category.Movement, state = CheckState.STABLE)
public class StepA extends Check {

    private int airTicks;

    public void onMove(PacketReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        // TODO: Handle damage properly and don't simply exempt.

        final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.FLYING, ExemptType.GLIDE, ExemptType.PLACE, ExemptType.NEAR_VEHICLE, ExemptType.PISTON, ExemptType.WEB, ExemptType.SLAB, ExemptType.STAIRS, ExemptType.BLOCK_ABOVE);

        if(!exempt && !gp.onLowBlock) {
            if(motionY > 0 && airTicks < 4) airTicks++;
            if(motionY >= 0) {
                if (motionY == 0 && gp.playerGround) {
                    if(airTicks < 4 && airTicks != 0 && airTicks > 0) fail(packet, "Stepped up a block wrongly", "airTicks §9" + airTicks);
                    else if(airTicks != 0 && airTicks > 0) removeBuffer();
                    airTicks = 0;
                }
            }
        }

        if(exempt || gp.onLowBlock) {
            airTicks -= 2;
            removeBuffer();
        }

    }

}
