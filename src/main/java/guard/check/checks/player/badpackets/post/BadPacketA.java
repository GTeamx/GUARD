package guard.check.checks.player.badpackets.post;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@GuardCheckInfo(name = "BadPacket A", category = GuardCategory.Player, state = GuardCheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class BadPacketA extends GuardCheck {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (isPost(packet.getPacketId(), PacketType.Play.Client.USE_ENTITY)) fail(packet, "Post packet", "§9USE_ENTITY"); else removeBuffer();
    }

}
