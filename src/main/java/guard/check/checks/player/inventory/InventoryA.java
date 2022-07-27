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
    boolean inventoryState;
    int mBuffer;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.WINDOW_CLICK) {
            if(packet.getPlayer().isSprinting()) fail(packet, "Sprinted with an opened inventory", "NaN");
            if(packet.getPlayer().isSneaking()) fail(packet, "Sneaked with an opened inventory", "NaN");
            if(System.currentTimeMillis() - gp.lastAttack < 100) fail(packet, "Attacked with an opened inventory", "NaN");
            inventoryState = true;
        }
        if(packet.getPacketId() == PacketType.Play.Client.CLOSE_WINDOW) {
            inventoryState = false;
        }
    }

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.NEAR_VEHICLE, ExemptType.FLYING, ExemptType.LIQUID, ExemptType.GLIDE, ExemptType.VELOCITY, ExemptType.ICE);
        if(gp.getDistance(true) > 0) {
            if(inventoryState) mBuffer++;
            if(!inventoryState || exempt) mBuffer = 0;
            if(mBuffer > 10) fail(packet, "Moved with an opened inventory", "NaN");
        }
    }
}