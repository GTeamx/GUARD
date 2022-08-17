package guard.check.checks.player.inventory;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@GuardCheckInfo(name = "Inventory A", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class InventoryA extends GuardCheck {

    boolean invState;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.WINDOW_CLICK) {
            invState = true;
            if(packet.getPlayer().isSprinting()) fail(packet, "Sprinted with an opened inventory", "NaN");
            if(packet.getPlayer().isSneaking()) fail(packet, "Sneaked with an opened inventory", "NaN");
        } else if(packet.getPacketId() == PacketType.Play.Client.CLOSE_WINDOW) {
            invState = false;
        } else if(packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            if(invState) fail(packet, "Attacked with an opened inventory", "NaN");
        }

    }

}