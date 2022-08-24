package guard.check.checks.combat.aim.aimassist;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.MathUtils;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "AimAssist A", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0.5, maxBuffer = 3)
public class AimAssistA extends GuardCheck {
    double tempBuffer;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        double divisorYaw = MathUtils.getGcd((long) (deltaYaw * MathUtils.EXPANDER), (long) (lastDeltaYaw * MathUtils.EXPANDER));
        double divisorPitch = MathUtils.getGcd((long) (deltaPitch * MathUtils.EXPANDER), (long) (lastDeltaPitch * MathUtils.EXPANDER));
        double constantYaw = divisorYaw / MathUtils.EXPANDER;
        double constantPitch = divisorPitch / MathUtils.EXPANDER;
        double currentX = deltaYaw / constantYaw;
        double currentY = deltaPitch / constantPitch;
        double previousX = lastDeltaYaw / constantYaw;
        double previousY = lastDeltaPitch / constantPitch;
        if (deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 10.0 && deltaPitch < 10.0) {
            double moduloX = currentX % previousX;
            double moduloY = currentY % previousY;
            double floorModuloX = Math.abs(Math.floor(moduloX) - moduloX);
            double floorModuloY = Math.abs(Math.floor(moduloY) - moduloY);
            boolean invalidX = (moduloX > 90.0D && floorModuloX > 0.1D);
            boolean invalidY = (moduloY > 90.0D && floorModuloY > 0.1D);
            if(invalidY && !gp.isCinematic) fail(packet, "Smooth Rotation", "iY");
            else removeBuffer();
            if(invalidX && invalidY) {
                tempBuffer = maxBuffer;
                maxBuffer = 0;
                fail(packet, "Smooth Rotation", "iY + iX");
            } else maxBuffer = tempBuffer;
        }
    }
}
