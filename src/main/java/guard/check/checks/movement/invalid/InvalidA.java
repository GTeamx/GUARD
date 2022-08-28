package guard.check.checks.movement.invalid;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Invalid A", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class InvalidA extends GuardCheck {

    double lastDistance = -10;
    int groundTicks;
    double maxSpeed = 0.302;

    @Override
    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        if(!gp.inAir) groundTicks = 0;
        if(gp.inAir) groundTicks++;

        if (groundTicks > 3) maxSpeed = 0.1;
        else maxSpeed = 0.311;

        debug("cM=" + (gp.getDistance(true) - lastDistance) + " mS=" + maxSpeed);
        if(!isExempt(ExemptType.TELEPORT) && lastDistance != -10) if(gp.getDistance(true) - lastDistance > maxSpeed) fail(packet, "Impossible Movement", "m=" + (gp.getDistance(true) - lastDistance) + "/" + maxSpeed);
        lastDistance = gp.getDistance(true);

    }
}
