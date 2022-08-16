package guard.check.checks.player.badpackets.post;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@GuardCheckInfo(name = "BadPacket F", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class BadPacketF extends GuardCheck {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (isPost(packet.getPacketId(), PacketType.Play.Client.WINDOW_CLICK)) fail(packet, "Post packet", "WINDOW_CLICK"); else removeBuffer();
    }

}
