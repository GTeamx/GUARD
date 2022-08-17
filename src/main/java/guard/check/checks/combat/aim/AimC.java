package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.MathUtils;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import org.bukkit.Bukkit;

@GuardCheckInfo(name = "Aim C", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0.25, maxBuffer = 3)
public class AimC extends GuardCheck {

    int buf;
    int buf2;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {

            final double deltaPitch = gp.deltaPitch;
            final double lastDeltaPitch = gp.lastDeltaPitch;
            final double deltaYaw = gp.deltaYaw;
            final double lastDeltaYaw = gp.lastDeltaYaw;

            if(deltaPitch != 0 && deltaYaw != 0) {

                final double expendedPitch = deltaPitch * MathUtils.EXPANDER;
                final double lastExpendedPitch = lastDeltaPitch * MathUtils.EXPANDER;

                final double expendedYaw = deltaYaw * MathUtils.EXPANDER;
                final double lastExpendedYaw = lastDeltaYaw * MathUtils.EXPANDER;

                final double dvP = MathUtils.getGcd(expendedPitch, lastExpendedPitch);
                final double dvY = MathUtils.getGcd(expendedYaw, lastExpendedYaw);

                final double ctP = dvP / MathUtils.EXPANDER;
                final double ctY = dvY / MathUtils.EXPANDER;

                final double cP = deltaPitch / ctP;
                final double cY = deltaYaw / ctY;
                final double lcP = lastDeltaPitch / ctP;
                final double lcY = lastDeltaYaw / ctY;

                final double mP = cP % lcP;
                final double mY = cY % lcY;

                final double fmP = Math.abs(Math.floor(mP) - mY);
                final double fmY = Math.abs(Math.floor(mY) - mP);
                
                final String sFMP = String.valueOf(fmP);
                final String sFMY = String.valueOf(fmY);
                final String sMP = String.valueOf(mP);
                final String sMY = String.valueOf(mY);
                
                if(sFMP.contains("0000") || sFMY.contains("0000") || sMY.contains("0000") || sMP.contains("0000")) {
                    buf++;
                    if(buf > 6) fail(null, "Rounded Rotations", "fmP=" + fmP + " fmY=" + fmY + " mP=" + mP + " mY=" + mY);
                } else if(buf > 0) buf--;


                if(fmP == 0.0 || fmP == 1.0 || fmP == 2.0 || fmP == 3.0 || fmY == 0.0 || fmY == 1.0 || fmY == 2.0 || fmY == 3.0) {
                    buf2++;
                    if(buf2 > 6) fail(null, "Rounded Rotation", "fmP=" + fmP + " fmY=" + fmY);
                } else buf2 = 0;

                if(Math.abs(deltaPitch) < 3 && Math.abs(deltaYaw) < 3) {
                    if(fmP > 70 || fmY > 70) fail(null, "Impossible Rotation", "fmP=" + fmP + " fmY=" + fmY + " dP=" + deltaPitch + " dY=" + deltaYaw);
                }

            }

        }
    }

}
