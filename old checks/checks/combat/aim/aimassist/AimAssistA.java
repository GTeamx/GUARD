package guard.check.checks.combat.aim.aimassist;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.MathUtils;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "AimAssist A", category = GuardCategory.Combat, state = GuardCheckState.STABLE, addBuffer = 1, removeBuffer = 0.20, maxBuffer = 3)
public class AimAssistA extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        final double yawGCD = MathUtils.getGcd((long) (deltaYaw * MathUtils.EXPANDER), (long) (lastDeltaYaw * MathUtils.EXPANDER));
        final double pitchGCD = MathUtils.getGcd((long) (deltaPitch * MathUtils.EXPANDER), (long) (lastDeltaPitch * MathUtils.EXPANDER));
        if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 10.0 && deltaPitch < 10.0) {
            final double yawGCDModulo = (deltaYaw / (yawGCD / MathUtils.EXPANDER)) % (lastDeltaYaw / (yawGCD / MathUtils.EXPANDER));
            final double pitchGCDModulo = (deltaPitch / (pitchGCD / MathUtils.EXPANDER)) % (lastDeltaPitch / (pitchGCD / MathUtils.EXPANDER));
            final double yawGCDModuloFloor = Math.abs(Math.floor(yawGCDModulo) - yawGCDModulo);
            final double pitchGCDModuloFloor = Math.abs(Math.floor(pitchGCDModulo) - pitchGCDModulo);
            if((pitchGCDModulo > 90 && pitchGCDModuloFloor > 0.1) && !gp.isCinematic) fail(packet, "Impossible smooth rotation", "A=§aTRUE");
            else removeBuffer();
            if((yawGCDModulo > 90 && yawGCDModuloFloor > 0.1) && (pitchGCDModulo > 90 && pitchGCDModuloFloor > 0.1)) fail(packet, "Impossible smooth rotation", "yGCDM §9" + yawGCDModulo + "\n" + " §8»§f yGCDMf §9" + yawGCDModuloFloor + "\n" + " §8»§f pGCDM §9" + pitchGCDModulo + "\n" + " §8»§f pGCDMf §9" + pitchGCDModuloFloor);
        }
    }
}
