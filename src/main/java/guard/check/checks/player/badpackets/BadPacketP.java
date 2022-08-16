package guard.check.checks.player.badpackets;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.packet.TransactionPacketClient;
import guard.utils.packet.TransactionPacketServer;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;

@GuardCheckInfo(name = "BadPacket P", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 1, maxBuffer = 1)
public class BadPacketP extends GuardCheck {
    boolean cSwing;
    long lastPacket;
    long lastId;
    int ping;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getNMSPacket());
            debug("cSwing=" + cSwing + " transactionPing=" + gp.transactionPing + " ping=" + ping);
            if (wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK && !cSwing && gp.transactionPing >= ping - 10) fail(packet, "Missing packet", "ARM_ANIMATION ? USE_ENTITY");
            else removeBuffer();
            cSwing = false;
        }
        if (packet.getPacketId() == PacketType.Play.Client.ARM_ANIMATION) cSwing = true;
    }



    public void onTransaction(TransactionPacketClient packet, boolean found) {
        if (lastId == packet.getId()) {
            ping = (int) (packet.getTimeStamp() - lastPacket);
        }
    }



    public void onTransactionSend(TransactionPacketServer packet) {
        lastPacket = packet.getTimeStamp();
        lastId = packet.getId();
    }
}
