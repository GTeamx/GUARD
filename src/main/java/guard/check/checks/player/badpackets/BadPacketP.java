package guard.check.checks.player.badpackets;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.packet.TransactionPacketClient;
import guard.utils.packet.TransactionPacketServer;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@GuardCheckInfo(name = "BadPacket P", category = GuardCategory.Player, state = GuardCheckState.STABLE, addBuffer = 1, removeBuffer = 0.5, maxBuffer = 1)
public class BadPacketP extends GuardCheck {

    boolean cSwing;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.ARM_ANIMATION) cSwing = true;
        if (packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getNMSPacket());
            debug("cSwing=" + cSwing);
            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK && !cSwing) fail(packet, "Invalid packet", "§9ARM_ANIMATION ? USE_ENTITY");
            else removeBuffer();
            cSwing = false;
        }
    }
}
