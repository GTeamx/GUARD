package guard.check.checks.player.badpackets;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@GuardCheckInfo(name = "BadPacket P", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 0.5, removeBuffer = 0, maxBuffer = 40)
public class BadPacketP extends GuardCheck {
    int packetCount;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.POSITION || packet.getPacketId() == PacketType.Play.Client.LOOK || packet.getPacketId() == PacketType.Play.Client.POSITION_LOOK) {
            packetCount++;
            if (packetCount > 0 || gp.getDistance(false) <= 0.1) removeBuffer();
        }else if (packet.getPacketId() == PacketType.Play.Client.FLYING) packetCount = -1;
        if (packetCount <= 0 && gp.getDistance(false) > 0.1D) fail(packet, "Packet spam", "pAmt=" + packetCount);
    }

}
