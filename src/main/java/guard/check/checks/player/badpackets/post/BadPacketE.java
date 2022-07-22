package guard.check.checks.player.badpackets.post;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@GuardCheckInfo(name = "BadPacket E", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class BadPacketE extends GuardCheck {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.HELD_ITEM_SLOT) {
            boolean isPost = isPost(packet.getPacketId(), (byte) -100);
            if (isPost) fail(packet, "Post packet", "HELD_ITEM_SLOT");
            if (isPost) packet.setCancelled(true);
        }
    }

}
