package guard.check.checks.player.badpackets;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.helditemslot.WrappedPacketInHeldItemSlot;

@GuardCheckInfo(name = "BadPacket G", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class BadPacketG extends GuardCheck {

    int lastSlot = -1;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.HELD_ITEM_SLOT) {
            WrappedPacketInHeldItemSlot p = new WrappedPacketInHeldItemSlot(packet.getNMSPacket());
            if (p.getCurrentSelectedSlot() == lastSlot) fail(packet, "Invalid action", "HELD_ITEM_SLOT_REPEATED");
            lastSlot = p.getCurrentSelectedSlot();
        }
    }

}
