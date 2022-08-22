package guard.check.checks.world.scaffold;

import guard.Guard;
import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockplace.WrappedPacketInBlockPlace;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.concurrent.FutureTask;

@GuardCheckInfo(name = "Scaffold A", category = GuardCategory.World, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 1, maxBuffer = 1)
public class ScaffoldA extends GuardCheck {

    boolean placedBlock;
    long lastBlock;


    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.BLOCK_PLACE) {
            WrappedPacketInBlockPlace place = new WrappedPacketInBlockPlace(packet.getNMSPacket());
            if(place.getItemStack().isPresent()) {
                if(place.getItemStack().get().getType().isBlock()) {
                    placedBlock = true;
                    lastBlock = System.currentTimeMillis();
                }
            }
        }
        if(packet.getPacketId() == PacketType.Play.Client.USE_ITEM) {
            if(gp.getPlayer().getInventory().getItemInMainHand().getType().isBlock() || gp.getPlayer().getInventory().getItemInOffHand().getType().isBlock()) {
                placedBlock = true;
                lastBlock = System.currentTimeMillis();
            }
        }
    }

    boolean wasAboveBlock;
    boolean wasSneaking;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if(getBlock(gp.getPlayer().getLocation().clone().subtract(0,1,0)).getType() != Material.AIR) {
            wasAboveBlock = true;
            wasSneaking = gp.getPlayer().isSneaking();
        } else {
            if(wasAboveBlock) {
                if(!wasSneaking && gp.player.isSneaking() && System.currentTimeMillis() - lastBlock < 600) {
                    fail(null, "Used Eagle", "EAGLE");
                } else removeBuffer();
            }
            wasSneaking = false;
            wasAboveBlock = false;
        }
    }

    //Taken from Fiona
    public Block getBlock(final Location location) {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return location.getBlock();
        } else {
            FutureTask<Block> futureTask = new FutureTask<>(() -> {
                location.getWorld().loadChunk(location.getBlockX() >> 4, location.getBlockZ() >> 4);
                return location.getBlock();
            });
            Bukkit.getScheduler().runTask(Guard.instance, futureTask);
            try {
                return futureTask.get();
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
            return null;
        }
    }
}
