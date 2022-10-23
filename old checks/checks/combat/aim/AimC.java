package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@GuardCheckInfo(name = "Aim C", category = GuardCategory.Combat, state = GuardCheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 1, maxBuffer = 4)
public class AimC extends GuardCheck {

    double previousPitch;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {

            if(gp.deltaYaw != 0) {
                if (Math.abs(gp.getPlayer().getLocation().getPitch() - previousPitch) < 0.1) fail(packet, "Repeated pitch pattern", "result §9" + Math.abs(gp.getPlayer().getLocation().getPitch() - previousPitch));
                else removeBuffer();
                previousPitch = gp.getPlayer().getLocation().getPitch();
            }
        }
    }
}
