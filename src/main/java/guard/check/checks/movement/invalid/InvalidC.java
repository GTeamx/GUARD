package guard.check.checks.movement.invalid;

import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Invalid C", category = Category.MOVEMENT)
public class InvalidC extends Check {

    int AirTicks;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {
        boolean exempt = isExempt(ExemptType.FLYING, ExemptType.LIQUID, ExemptType.CLIMBABLE, ExemptType.NEAR_VEHICLE, ExemptType.SLAB, ExemptType.STAIRS, ExemptType.PISTON, ExemptType.PLACE);
        if(motionY > 0.05) AirTicks++;
        if(motionY <= 0.05) AirTicks = 0;
        if(AirTicks > 7 && !exempt) fail("Added AirTicks", AirTicks);
    }
}