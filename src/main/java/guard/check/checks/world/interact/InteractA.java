package guard.check.checks.world.interact;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Objects;

@GuardCheckInfo(name = "Interact A", category = GuardCategory.World, state = GuardCheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class InteractA extends GuardCheck {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.BLOCK_DIG) {
            WrappedPacketInBlockDig wrapper = new WrappedPacketInBlockDig(packet.getNMSPacket());

            final Location blockLoc = new Location(gp.getPlayer().getWorld(), wrapper.getBlockPosition().getX(), wrapper.getBlockPosition().getY(), wrapper.getBlockPosition().getZ());
            final Block block = Objects.requireNonNull(blockLoc.getWorld()).getBlockAt(blockLoc);

            if(block.isLiquid()) fail(packet, "Interacted with a liquid", "iL=§aTRUE");
        }
    }

}
