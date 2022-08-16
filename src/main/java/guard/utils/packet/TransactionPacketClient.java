package guard.utils.packet;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.pong.WrappedPacketInPong;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import lombok.Getter;

@Getter
public class TransactionPacketClient {

    public long timeStamp;
    public WrappedPacketInTransaction transaction;
    public WrappedPacketInPong ping;

    public TransactionPacketClient(WrappedPacketInPong packet, long timeStamp) {
        this.ping = packet;
        this.timeStamp = timeStamp;
    }

    public TransactionPacketClient(WrappedPacketInTransaction packet, long timeStamp) {
        this.transaction = packet;
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return PacketEvents.get().getServerUtils().getVersion().isNewerThanOrEquals(ServerVersion.v_1_17) ? ping.getId() : transaction.getWindowId();
    }
}
