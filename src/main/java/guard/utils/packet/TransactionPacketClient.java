package guard.utils.packet;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPong;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;
import lombok.Getter;

@Getter
public class TransactionPacketClient {

    public long timeStamp;
    public WrapperPlayClientWindowConfirmation transaction;
    public WrapperPlayClientPong ping;

    public TransactionPacketClient(WrapperPlayClientPong packet, long timeStamp) {
        this.ping = packet;
        this.timeStamp = timeStamp;
    }

    public TransactionPacketClient(WrapperPlayClientWindowConfirmation packet, long timeStamp) {
        this.transaction = packet;
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_17) ? ping.getId() : transaction.getWindowId();
    }
}
