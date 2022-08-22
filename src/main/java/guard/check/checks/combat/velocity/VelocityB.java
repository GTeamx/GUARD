package guard.check.checks.combat.velocity;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import guard.utils.BoundingBox;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.out.entityvelocity.WrappedPacketOutEntityVelocity;
import io.github.retrooper.packetevents.utils.vector.Vector3d;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

@GuardCheckInfo(name = "Velocity B", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 1, maxBuffer = 3)
public class VelocityB extends GuardCheck {

    Vector3d veloVector;
    public int ticks;
    boolean hit;

    public void onPacketSend(PacketPlaySendEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Server.ENTITY_VELOCITY) {
            WrappedPacketOutEntityVelocity velo = new WrappedPacketOutEntityVelocity(packet.getNMSPacket());
            if(velo.getEntityId() == gp.player.getEntityId()) {
                veloVector = velo.getVelocity();
                hit = true;
            }
        }
    }


    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        final BoundingBox boundingBox = new BoundingBox(gp.getPlayer()).expand(0.5, 0, 0.5);
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
        if(b.stream().allMatch(block -> block.getType() == Material.AIR)) {
            boolean lastPG = gp.lastPlayerGround;
            boolean jumped = !gp.playerGround && lastPG;
            if(hit) {
                if(jumped || (ticks++ > (4 + gp.transactionPing / 50))) {
                    double predictedVelocityXZ =  Math.hypot(veloVector.x, veloVector.z);;
                    double percentage = gp.getDeltaXZ() / predictedVelocityXZ * 100;
                    double percentage2 = gp.getLastDeltaXZ() / predictedVelocityXZ * 100;
                    debug("percentage=" + percentage + " percentage2=" + percentage2);
                    if ((percentage < 30) && percentage >= 0 && !String.valueOf(percentage).equals("-0.0")) { // TODO: check if player is moving, if yes then check the next not the current flying
                        fail(null, "Velocity Ignored", "percentage=" + percentage);
                    } else removeBuffer();
                    ticks = 0;
                    hit = false;
                }
            }

        }
    }
}
