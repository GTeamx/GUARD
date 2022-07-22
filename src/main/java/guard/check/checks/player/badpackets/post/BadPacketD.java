package guard.check.checks.player.badpackets.post;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@GuardCheckInfo(name = "BadPacket D", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class BadPacketD extends GuardCheck {

    public void onPacket(PacketPlayReceiveEvent packet) {
        boolean isPost = isPost(packet.getPacketId(), PacketType.Play.Client.CUSTOM_PAYLOAD);
        if (isPost) fail(packet, "Post packet", "CUSTOM_PAYLOAD");
    }

}
