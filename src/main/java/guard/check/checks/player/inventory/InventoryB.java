package guard.check.checks.player.inventory;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@GuardCheckInfo(name = "Inventory B", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0, maxBuffer = 10)
public class InventoryB extends GuardCheck {

    boolean inventoryState;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.WINDOW_CLICK) {
            inventoryState = true;
        }
        if(packet.getPacketId() == PacketType.Play.Client.CLOSE_WINDOW) {
            inventoryState = false;
        }
    }

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.NEAR_VEHICLE, ExemptType.FLYING, ExemptType.LIQUID, ExemptType.GLIDE, ExemptType.VELOCITY, ExemptType.ICE);
        if(gp.getDistance(true) > 0) {
            if(inventoryState) fail(packet, "Moved with an opened inventory", "NaN");;
            if(!inventoryState || exempt) removeBuffer();

        }
    }
}
