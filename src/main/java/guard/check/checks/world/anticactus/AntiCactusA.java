package guard.check.checks.world.anticactus;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.BoundingBox;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

@GuardCheckInfo(name = "AntiCactus A", category = GuardCategory.World, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class AntiCactusA extends GuardCheck {

    int moves;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        final BoundingBox boundingBox = new BoundingBox(gp.getPlayer()).expand(0.001, 0, 0.001);
        final double minX = boundingBox.getMinX();
        final double minY = boundingBox.getMinY();
        final double minZ = boundingBox.getMinZ();
        final double maxX = boundingBox.getMaxX();
        final double maxY = boundingBox.getMaxY();
        final double maxZ = boundingBox.getMaxZ();
        List<Block> b = new ArrayList<>();
        for (double x = minX; x <= maxX; x += (maxX - minX)) {
            for (double y = minY; y <= maxY + 0.01; y += (maxY - minY) / 5) {
                for (double z = minZ; z <= maxZ; z += (maxZ - minZ)) {
                    final Location location = new Location(gp.getPlayer().getWorld(), x, y, z);
                    final Block block = gp.getBlock(location);
                    b.add(block);
                }
            }
        }
        if(b.stream().anyMatch(block -> block.getType() == Material.CACTUS)) {
            if (System.currentTimeMillis() - gp.lastCactusDamage > 1000) {
                moves++;
                if (moves > 20) {
                    fail(null, "AntiCactus Prediction Gone Wrong", "time=" + (System.currentTimeMillis() - gp.lastCactusDamage) + " moves="+ moves);
                }
            } else moves = 0;
        } else {
            if(moves > 0) {
                moves -= 1;
            }
        }
    }
}
