package guard.check.checks.movement.speed;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Speed E", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 1, maxBuffer = 1)
public class SpeedE extends GuardCheck {
    int airTicks;
    double accel;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if(!gp.playerGround) airTicks++;
        if(gp.playerGround) airTicks = 0;
        accel = 1e-12;
        if(System.currentTimeMillis() - gp.lastAttack < 1200) accel = 0.006;
        final double prediction = gp.getLastDeltaXZ() * 0.91F + (gp.player.isSprinting() ? 0.026 : 0.02);
        final double difference = gp.getDeltaXZ() - prediction;
        final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.NEAR_VEHICLE);

        if (difference > accel && airTicks > 2 && !exempt) fail(packet, "Predictions unfollowed", "diff=" + difference);
        else buffer = Math.max(buffer - 1, 0);
    }

}
