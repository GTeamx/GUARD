package guard.check.checks.movement.step;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Step B", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0, maxBuffer = 5)
public class StepB extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.PLACE, ExemptType.VELOCITY, ExemptType.STAIRS, ExemptType.GLIDE, ExemptType.SLAB, ExemptType.SLIME, ExemptType.FLYING, ExemptType.NEAR_VEHICLE, ExemptType.INSIDE_VEHICLE, ExemptType.TELEPORT, ExemptType.LIQUID, ExemptType.PLACE);
        if(motionY > 0.117600002289 && !gp.onLowBlock && !exempt) fail(packet, "Added ticks", "mY=" + motionY);
        if(motionY <= 0.117600002289 && !gp.isInAir() || gp.playerGround) removeBuffer();

    }

}
