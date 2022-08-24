package guard.check.checks.combat.killaura;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.github.retrooper.packetevents.packetwrappers.play.out.animation.WrappedPacketOutAnimation;

@GuardCheckInfo(name = "KillAura B", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 1, maxBuffer = 5)
public class KillAuraB extends GuardCheck {

    double lastDistance;
    int hitTicks;

    @Override
    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if(gp.player.isSprinting()) {
            double accel = Math.abs(gp.getDeltaXZ() - gp.getLastDeltaXZ());
            if(hitTicks++ <= 2) {
                if(accel < 0.025) {
                    fail(null, "Invalid Accel", "accel=" + accel);
                    debug("accel=" + accel);
                } else {
                    removeBuffer();
                }
            } else {
                removeBuffer();
            }
        } else {
            removeBuffer();
        }
    }


    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            WrappedPacketInUseEntity ue = new WrappedPacketInUseEntity(packet.getNMSPacket());
            if(ue.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                hitTicks = 0;
            }
        }
    }
}
