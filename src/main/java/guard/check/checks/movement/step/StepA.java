package guard.check.checks.movement.step;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Step A", category = GuardCategory.Movement, state = GuardCheckState.STABLE)
public class StepA extends GuardCheck {

    private int airTicks;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

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
