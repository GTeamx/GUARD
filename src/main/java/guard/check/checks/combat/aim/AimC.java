package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.MathUtils;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import org.bukkit.Bukkit;

@GuardCheckInfo(name = "Aim C", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0.25, maxBuffer = 3.25)
public class AimC extends GuardCheck {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {

            if(gp.deltaPitch != 0 && gp.deltaYaw != 0) {

                final double ctP = (MathUtils.getGcd(gp.deltaPitch * MathUtils.EXPANDER, gp.lastDeltaPitch * MathUtils.EXPANDER)) / MathUtils.EXPANDER;
                final double ctY = (MathUtils.getGcd(gp.deltaYaw * MathUtils.EXPANDER, gp.lastDeltaYaw * MathUtils.EXPANDER)) / MathUtils.EXPANDER;
                final double fmP = Math.abs(Math.floor(gp.deltaPitch / ctP % gp.lastDeltaPitch / ctP) - (gp.deltaYaw / ctY % gp.lastDeltaYaw / ctY));
                if (Math.abs(fmP / (gp.deltaPitch * MathUtils.EXPANDER)) < 7E-10) fail(null, "Impossible Rotation", "fMp=" + Math.abs(fmP / (gp.deltaPitch * MathUtils.EXPANDER)));
                else removeBuffer();
            }

        }
    }
}
