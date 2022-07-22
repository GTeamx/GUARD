package guard.check.checks.player.badpackets;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.keepalive.WrappedPacketInKeepAlive;
import io.github.retrooper.packetevents.packetwrappers.play.out.keepalive.WrappedPacketOutKeepAlive;

@GuardCheckInfo(name = "BadPacket M", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class BadPacketM extends GuardCheck {

    long id;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.KEEP_ALIVE) {
            WrappedPacketInKeepAlive keepalive = new WrappedPacketInKeepAlive(packet.getNMSPacket());
            if(id != keepalive.getId()) {
                fail(null, "Invalid packet", "id=" + id + " fakeid=" + keepalive.getId());
            }
        }
    }

    public void onPacketSend(PacketPlaySendEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Server.KEEP_ALIVE) {
            WrappedPacketOutKeepAlive keepalive = new WrappedPacketOutKeepAlive(packet.getNMSPacket());
            id = keepalive.getId();
        }
    }
}
