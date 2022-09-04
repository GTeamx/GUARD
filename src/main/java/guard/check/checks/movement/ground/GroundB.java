package guard.check.checks.movement.ground;

import guard.Guard;
import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import guard.utils.BoundingBox;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;

@GuardCheckInfo(name = "Ground B", category = GuardCategory.Movement, state = GuardCheckState.EXPERIMENTAL, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class GroundB extends GuardCheck {


    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        final BoundingBox boundingBox = new BoundingBox(gp.getPlayer()).expandOther(1, 1, 0.55, 0.6, 1, 1);
        List<Block> b = new ArrayList<>();
        for (double x = boundingBox.getMinX(); x <= boundingBox.getMaxX(); x += (boundingBox.getMaxX() - boundingBox.getMinX())) {
            for (double y = boundingBox.getMinY(); y <= boundingBox.getMaxY() + 0.01; y += (boundingBox.getMaxY() - boundingBox.getMinY()) / 5) {
                for (double z = boundingBox.getMinZ(); z <= boundingBox.getMaxZ(); z += (boundingBox.getMaxZ() - boundingBox.getMinZ())) {
                    final Location location = new Location(gp.getPlayer().getWorld(), x, y, z);
                    final Block block = this.getBlock(location);
                    b.add(block);
                }
            }
        }

        boolean exempt = isExempt(ExemptType.NEAR_VEHICLE, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.CLIMBABLE, ExemptType.JOINED, ExemptType.STAIRS, ExemptType.VELOCITY);
        debug("s=" + gp.serverGround + " c=" + gp.playerGround + " air=" + gp.inAir);
        if(gp.serverGround && gp.playerGround && getBlock(gp.player.getLocation().clone().subtract(0,1,0)).getType() == Material.AIR && gp.inAir && !exempt && b.stream().allMatch(block -> block.getType() == Material.AIR)) fail(packet, "Spoofed ground state", "s=§aTRUE" + "\n"  + " §8»§f c=§aTRUE");
        else removeBuffer();
    }

    //Taken from Fiona, credits to Fiona
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
