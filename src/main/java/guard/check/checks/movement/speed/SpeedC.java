package guard.check.checks.movement.speed;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.check.CheckState;
import guard.exempt.ExemptType;

@CheckInfo(name = "Speed C", category = Category.Movement, state = CheckState.STABLE, addBuffer = 1, removeBuffer = 1, maxBuffer = 2)
public class SpeedC extends Check {

    private int airTicks;
    private double accel = 1e-12;
    private boolean isSprinting;

    public void onMove(PacketReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        final boolean exempt = isExempt(ExemptType.FLYING, ExemptType.NEAR_VEHICLE);

        // We're too poor to afford to make airTicks in GuardPlayer
        if(!gp.playerGround) airTicks++;
        if(gp.playerGround) airTicks = 0;

        // Handle damage
        if(System.currentTimeMillis() - gp.lastAttack < 1200) accel = 0.04;

        final double prediction = gp.getLastDeltaXZ() * 0.91F + (isSprinting ? 0.026 : 0.02);
        final double difference = gp.getDeltaXZ() - prediction;

        if (difference > accel && airTicks > 2 && !exempt && !gp.isInLiquid) fail(packet, "Impossible movement friction", "difference §9" + difference);
        else removeBuffer();
    }

    // Sprint desync fix
    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            WrapperPlayClientEntityAction action = new WrapperPlayClientEntityAction(packet);
            if(action.getAction() == WrapperPlayClientEntityAction.Action.START_SPRINTING) isSprinting = true;
            if(action.getAction() == WrapperPlayClientEntityAction.Action.STOP_SPRINTING) isSprinting = false;
        }
    }

}
