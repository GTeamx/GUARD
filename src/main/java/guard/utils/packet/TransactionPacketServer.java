package guard.utils.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;
import lombok.Getter;

@Getter
public class TransactionPacketServer {
    
    public long timeStamp;
    public WrapperPlayServerWindowConfirmation transaction;
    public WrapperPlayServerPing ping;

    public TransactionPacketServer(WrapperPlayServerWindowConfirmation packet, long timeStamp) {
        this.transaction = packet;
        this.timeStamp = timeStamp;
    }

    public TransactionPacketServer(WrapperPlayServerPing packet, long timeStamp) {
        this.ping = packet;
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_17) ? ping.getId() : transaction.getWindowId();
    }
}
