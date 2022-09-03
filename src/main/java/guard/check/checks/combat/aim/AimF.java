package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@GuardCheckInfo(name = "Aim F", category = GuardCategory.Combat, state = GuardCheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0, maxBuffer = 3)
public class AimF extends GuardCheck {

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
            if(GCD < 0.149999 && GCD != 0 && !gp.isCinematic) fail(packet, "Didn't follow GCD", "gcd §9" + GCD);
            if(GCD >= 0.149999 && GCD < 10) removeBuffer();
            if(GCD > 10 && !gp.isCinematic) fail(packet, "Impossible rotation speed", "rot §9" + GCD);
            debug("gcd=" + GCD + " c=" + gp.isCinematic + " b=" + buffer);
        }
    }
}
