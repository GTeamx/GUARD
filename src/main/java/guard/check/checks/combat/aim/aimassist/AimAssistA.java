package guard.check.checks.combat.aim.aimassist;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.MathUtils;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@GuardCheckInfo(name = "AimAssist A", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0, maxBuffer = 6)
public class AimAssistA extends GuardCheck {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {

            if (gp.deltaPitch > 1 && gp.deltaYaw != 0) {

                final double ctP = (MathUtils.getGcd(gp.deltaPitch * MathUtils.EXPANDER, gp.lastDeltaPitch * MathUtils.EXPANDER)) / MathUtils.EXPANDER;
                final double ctY = (MathUtils.getGcd(gp.deltaYaw * MathUtils.EXPANDER, gp.lastDeltaYaw * MathUtils.EXPANDER)) / MathUtils.EXPANDER;
                final double fmP = Math.abs(Math.floor(gp.deltaPitch / ctP % gp.lastDeltaPitch / ctP) - (gp.deltaYaw / ctY % gp.lastDeltaYaw / ctY));
                final double math = ((fmP - gp.deltaPitch) * MathUtils.EXPANDER) - (Math.cbrt(fmP) * Math.hypot(ctP, ctY));
                if(String.valueOf(math).contains("E8")) fail(packet, "Perfect Rotations", "m=" + math);
                else if(!String.valueOf(math).contains("E7")) removeBuffer();
                if(String.valueOf(math).contains("E9")) {
                    maxBuffer = 0;
                    fail(packet, "Perfect Rotations", "m=" + math);
                } else maxBuffer = 6;
            }
        }
    }

}
