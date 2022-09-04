package guard.check.checks.combat.killaura;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@GuardCheckInfo(name = "Killaura A", category = GuardCategory.Combat, state = GuardCheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class KillAuraA extends GuardCheck {

    public double swings;
    public double hits;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            WrappedPacketInUseEntity ue = new WrappedPacketInUseEntity(packet.getNMSPacket());
            if(ue.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
                if(gp.target != null) {
                    if(gp.getDeltaXZ() > 0.15 && System.currentTimeMillis() - gp.lastAttack < 100 && Math.abs(gp.deltaYaw) > 1f)
                        hits++;
                }
            }
        }
        if(packet.getPacketId() == PacketType.Play.Client.ARM_ANIMATION) {
            swings++;
            if(swings > 50) {
                if(hits > 40) {
                    double accuracy = 100 - hits / swings;
                    debug("accuracy=" + accuracy + "%");
                    fail(packet, "Suspicious hit accuracy", "accuracy §9" + accuracy + "%");
                } else {
                    removeBuffer();
                }
                hits = 0;
                swings = 0;
            }
        }
    }

}
