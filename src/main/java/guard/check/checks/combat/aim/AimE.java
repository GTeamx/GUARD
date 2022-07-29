package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.MathUtils;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Aim E", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class AimE extends GuardCheck {
    float lastDeltaYaw, lastDeltaPitch;
    double divisorYaw, divisorPitch;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if (deltaYaw != 0 && deltaPitch != 0) {
            // Exempt Cinematic

            final float deltaY = deltaYaw % 360F;

            final long toDivide_1 = (long) (deltaY * MathUtils.EXPANDER);
            final long toDivide_2 = (long) (deltaPitch * MathUtils.EXPANDER);
            final long toDivide_3 = (long) (lastDeltaYaw * MathUtils.EXPANDER);
            final long toDivide_4 = (long) (lastDeltaPitch * MathUtils.EXPANDER);

            divisorYaw = MathUtils.getGcd(toDivide_1, toDivide_3);
            divisorPitch = MathUtils.getGcd(toDivide_2, toDivide_4);

            final double constantYaw = divisorYaw / MathUtils.EXPANDER;
            final double constantPitch = divisorPitch / MathUtils.EXPANDER;

            final double currentX = deltaY / constantYaw;
            final double currentY = deltaPitch / constantPitch;

            final double previousX = lastDeltaYaw / constantYaw;
            final double previousY = lastDeltaPitch / constantPitch;

            if (deltaY > 0.0 && deltaPitch > 0.0 && deltaY < 20.f && deltaPitch < 20.f) {
                final double moduloX = currentX % previousX;
                final double moduloY = currentY % previousY;

                final double floorModuloX = Math.abs(Math.floor(moduloX) - moduloX);
                final double floorModuloY = Math.abs(Math.floor(moduloY) - moduloY);

                final boolean invalidX = moduloX > 90.d && floorModuloX > 0.1;
                final boolean invalidY = moduloY > 90.d && floorModuloY > 0.1;

                final String info = String.format(
                        "mx=%.2f, my=%.2f, fmx=%.2f, fmy=%.2f",
                        moduloX, moduloY, floorModuloX, floorModuloY
                );

                debug(info);

                if (invalidX && invalidY) {
                    if (++buffer > 0) {
                        fail(packet, "Divisor Found", "iX=" + invalidX + " iY=" + invalidY);
                    }
                } else {
                    buffer -= buffer > 0 ? 1 : 0;
                }
            }

            this.lastDeltaYaw = deltaY;
            this.lastDeltaPitch = deltaPitch;
        }
    }

}
