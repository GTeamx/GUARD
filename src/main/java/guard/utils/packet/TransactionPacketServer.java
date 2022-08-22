package guard.utils.packet;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.packetwrappers.play.out.ping.WrappedPacketOutPing;
import io.github.retrooper.packetevents.packetwrappers.play.out.transaction.WrappedPacketOutTransaction;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import lombok.Getter;

@Getter
public class TransactionPacketServer {
    
    public long timeStamp;
    public WrappedPacketOutTransaction transaction;
    public WrappedPacketOutPing ping;

    public TransactionPacketServer(WrappedPacketOutTransaction packet, long timeStamp) {
        this.transaction = packet;
        this.timeStamp = timeStamp;
    }

    public TransactionPacketServer(WrappedPacketOutPing packet, long timeStamp) {
        this.ping = packet;
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return PacketEvents.get().getServerUtils().getVersion().isNewerThanOrEquals(ServerVersion.v_1_17) ? ping.getId() : transaction.getActionNumber();
    }
}
