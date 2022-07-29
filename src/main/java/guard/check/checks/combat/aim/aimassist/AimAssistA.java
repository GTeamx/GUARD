package guard.check.checks.combat.aim.aimassist;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.Location;

@GuardCheckInfo(name = "AimAssist A", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class AimAssistA extends GuardCheck {
    private int hits, swings;
    private Location lastLocation;
    private Float lastYaw, lastPitch;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {

            WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getNMSPacket());
            final float yaw = gp.getPlayer().getLocation().getYaw() % 360F;
            final float pitch = gp.getPlayer().getLocation().getPitch();
            final boolean isMoving = gp.getDeltaXZ() != 0;
            if (wrapper.getEntity() != null) {
                if (lastLocation != null && lastYaw != null && lastPitch != null) {
                    if (wrapper.getEntity().getLocation().distance(lastLocation) > 0.1
                            && lastYaw != yaw && lastPitch != pitch && isMoving
                            && wrapper.getEntity().getWorld() == gp.getPlayer().getWorld()) {
                        ++hits;
                    }
                }
                lastLocation = wrapper.getEntity().getLocation();
            }
            lastYaw = yaw;
            lastPitch = pitch;
        } else if (packet.getPacketId() == PacketType.Play.Client.ARM_ANIMATION) {
            debug("swings=" + swings);
            if (++swings >= 50) {
                final double accuracy = hits/swings;
                debug("accuracy=" + accuracy);
                if (hits > 40) {
                    fail(packet, "Suspicious Accuracy", "acc=" + accuracy);
                } else {
                    buffer = Math.max(buffer - 2, 0);
                }
                hits = swings = 0;
            }
        }
    }

}
