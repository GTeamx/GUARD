package guard.check.checks.movement.speed;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;

@GuardCheckInfo(name = "Speed E", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 1, maxBuffer = 1)
public class SpeedE extends GuardCheck {
    int airTicks;
    double accel;
    boolean isSprinting;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if(!gp.playerGround) airTicks++;
        if(gp.playerGround) airTicks = 0;
        accel = 1e-12;
        if(System.currentTimeMillis() - gp.lastAttack < 1200) accel = 0.04;
        final double prediction = gp.getLastDeltaXZ() * 0.91F + (isSprinting ? 0.026 : 0.02);
        final double difference = gp.getDeltaXZ() - prediction;
        final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.NEAR_VEHICLE, ExemptType.VELOCITY);

        if (difference > accel && airTicks > 2 && !exempt && !gp.isInLiquid) fail(packet, "Predictions unfollowed", "diff=" + difference);
        else removeBuffer();
    }


    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.ENTITY_ACTION) {
            WrappedPacketInEntityAction action = new WrappedPacketInEntityAction(packet.getNMSPacket());
            if(action.getAction() == WrappedPacketInEntityAction.PlayerAction.START_SPRINTING) {
                isSprinting = true;
            }
            if(action.getAction() == WrappedPacketInEntityAction.PlayerAction.STOP_SPRINTING) {
                isSprinting = false;
            }
        }
    }
}
