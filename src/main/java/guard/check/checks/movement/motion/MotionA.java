package guard.check.checks.movement.motion;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Motion A", category = GuardCategory.Movement, state = GuardCheckState.EXPERIMENTAL, addBuffer = 1, removeBuffer = 0.25, maxBuffer = 2)
public class MotionA extends GuardCheck {

    int jumpTicks = 0;

    @Override
    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        final boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.VELOCITY, ExemptType.SLIME, ExemptType.BLOCK_ABOVE, ExemptType.LIQUID, ExemptType.GLIDE, ExemptType.FLYING, ExemptType.FULL_LIQUID, ExemptType.NEAR_VEHICLE, ExemptType.PISTON, ExemptType.STAIRS, ExemptType.SLAB, ExemptType.CLIMBABLE);
        if (motionY > 0) jumpTicks++;
        else jumpTicks = 0;

        if (!exempt && !gp.onLowBlock) {
            if (jumpTicks == 1 && motionY != 0.41999998688697815) fail(packet, "Modified jump motion", "mY §9" + motionY + "\n" + " §8»§f predicted §90.41999998688697815" + "\n" + " §8»§f jumpTicks §9" + jumpTicks); else removeBuffer();
            if (jumpTicks == 2 && motionY != 0.33319999363422426) fail(packet, "Modified jump motion", "mY §9" + motionY + "\n" + " §8»§f predicted §90.33319999363422426" + "\n" + " §8»§f jumpTicks §9" + jumpTicks); else removeBuffer();
            if (jumpTicks == 3 && motionY != 0.24813599859094637) fail(packet, "Modified jump motion", "mY §9" + motionY + "\n" + " §8»§f predicted §90.24813599859094637" + "\n" + " §8»§f jumpTicks §9" + jumpTicks); else removeBuffer();
            if (jumpTicks == 4 && motionY != 0.16477328182607211) fail(packet, "Modified jump motion", "mY §9" + motionY + "\n" + " §8»§f predicted §90.16477328182607211" + "\n" + " §8»§f jumpTicks §9" + jumpTicks); else removeBuffer();
            if (jumpTicks == 5 && motionY != 0.08307781780646906) fail(packet, "Modified jump motion", "mY §9" + motionY + "\n" + " §8»§f predicted §90.08307781780646906" + "\n" + " §8»§f jumpTicks §9" + jumpTicks); else removeBuffer();
        }
    }
}
