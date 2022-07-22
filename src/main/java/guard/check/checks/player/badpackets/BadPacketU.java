package guard.check.checks.player.badpackets;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@GuardCheckInfo(name = "BadPacket U", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 1, maxBuffer = 3)
public class BadPacketU extends GuardCheck {
    double lastUse;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.BLOCK_PLACE) {
            if (lastUse - System.currentTimeMillis() > -50.0D && lastUse - System.currentTimeMillis() < -1.0D) fail(packet, "Packet spam", "delay=" + (lastUse - System.currentTimeMillis()));
            if (lastUse - System.currentTimeMillis() < -50.0D) removeBuffer();
            lastUse = System.currentTimeMillis();
        }
    }

}
