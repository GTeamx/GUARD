package guard.check.checks.player.badpackets;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@GuardCheckInfo(name = "BadPacket Q", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0, maxBuffer = 42)
public class BadPacketQ extends GuardCheck {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.FLYING) fail(packet, "Packet spam", "pAmt=" + buffer);
        else removeBuffer();
    }

}
