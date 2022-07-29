package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

import java.util.function.Predicate;

@GuardCheckInfo(name = "Aim C", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0.25, maxBuffer = 1)
public class AimC extends GuardCheck {
    final Predicate<Float> validRotation = rotation -> rotation > 3F && rotation < 35F;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if(deltaPitch != 0 || deltaYaw != 0) {
            final float deltaP = Math.abs(deltaPitch);
            final float deltaY = Math.abs(deltaYaw % 360F);

            final float pitch = Math.abs(gp.getPlayer().getLocation().getPitch());

            final boolean invalidPitch = deltaP < 0.009 && validRotation.test(deltaY);
            final boolean invalidYaw = deltaY < 0.009 && validRotation.test(deltaP);

            final boolean exempt = isExempt(ExemptType.INSIDE_VEHICLE);
            final boolean invalid = !exempt && (invalidPitch || invalidYaw) && pitch < 89F;

            if (invalid) {
                fail(packet, "Impossible Rotations", "dP=" + deltaP + " dY=" + deltaY);
            } else {
                removeBuffer();
            }
        }
    }

}
