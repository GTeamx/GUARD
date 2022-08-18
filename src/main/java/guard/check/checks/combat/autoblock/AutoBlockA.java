package guard.check.checks.combat.autoblock;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@GuardCheckInfo(name = "AutoBlock A", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class AutoBlockA extends GuardCheck {

    @Override
    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getNMSPacket());
            if((System.currentTimeMillis() - gp.lastBlockPlaced <= 1) && wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) fail(packet, "Invalid packet order", "plT=" + (System.currentTimeMillis() - gp.lastBlockPlaced));
        }
    }
}
