package guard.check.checks.combat.aim.aimassist;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@GuardCheckInfo(name = "AimAssist A", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0, maxBuffer = 3)
public class AimAssistA extends GuardCheck {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            if(gp.deltaPitch > 1 && gp.deltaYaw > 1) fail(packet, "Aiming pattern", "dP=" + gp.deltaPitch + " dY=" + gp.deltaYaw);
            else {
                removeBuffer();
            }
        }
    }

}
