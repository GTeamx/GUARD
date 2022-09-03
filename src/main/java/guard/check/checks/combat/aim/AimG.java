package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@GuardCheckInfo(name = "Aim G", category = GuardCategory.Combat, state = GuardCheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0, maxBuffer = 3)
public class AimG extends GuardCheck {

    public double customGCD(float current, float previous) {
        if(current < previous) return customGCD(previous, current);
        if(Math.abs(previous) < 0.001) return current;
        if(Math.abs(previous) > 0.001) return customGCD(previous, (float) (current - Math.floor(current / previous) * previous));
        return 0;
    }

    @Override
    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            double GCD = customGCD(Math.abs(gp.deltaPitch), Math.abs(gp.lastDeltaPitch));
            if(String.valueOf(GCD).length() < 16 && GCD != 0) fail(packet, "Impossible rounded rotation", "rot §9" + GCD);
            if(String.valueOf(GCD).length() >= 16) removeBuffer();
            debug("gcd=" + GCD + " c=" + gp.isCinematic + " b=" + buffer);
        }
    }
}
