package guard.check.checks.combat.killaura;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import org.bukkit.Location;

@GuardCheckInfo(name = "KillAura C", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class KillAuraC extends GuardCheck {
    private Location lastAttackerLocation;
    private float lastYaw, lastPitch;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {

            if (gp.getTarget() == null || packet.getPlayer() == null) return;
            if (gp.getTarget().getWorld() != packet.getPlayer().getWorld()) return;

            final Location attackerLocation = packet.getPlayer().getLocation();

            final float yaw = gp.getPlayer().getLocation().getYaw() % 360F;
            final float pitch = gp.getPlayer().getLocation().getPitch();

            if (lastAttackerLocation != null) {
                final boolean check = yaw != lastYaw &&
                        pitch != lastPitch &&
                        attackerLocation.distance(lastAttackerLocation) > 0.1;

                debug("buffer=" + buffer);

                if (check && !packet.getPlayer().hasLineOfSight(gp.getTarget())) {
                    if (buffer++ > 0) {
                        fail(packet, "Hit through walls", "NaN");
                    }
                } else {
                    buffer = Math.max(buffer - 1, 0);
                }
            }

            lastAttackerLocation = packet.getPlayer().getLocation();

            lastYaw = yaw;
            lastPitch = pitch;
        }
    }

}
