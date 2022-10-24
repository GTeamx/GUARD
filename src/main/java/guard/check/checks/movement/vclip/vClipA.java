package guard.check.checks.movement.vclip;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.potion.PotionEffectType;

@GuardCheckInfo(name = "vClip A", category = GuardCategory.Movement, state = GuardCheckState.STABLE)
public class vClipA extends GuardCheck {

    @Override
    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        // TODO: Add WATER_SOUlSAND and WATER_MAGMA exempt types.

        final boolean exempt = isExempt(ExemptType.GLIDE, ExemptType.NEAR_VEHICLE, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.FLYING);

        double maxMotion = isExempt(ExemptType.VELOCITY) ? 0.42: 0.42;
        maxMotion += (double)(gp.hasPotionEffect(PotionEffectType.JUMP) ? (gp.getEffectByType(PotionEffectType.JUMP).get().getAmplifier() + 1) * 0.1F : 0);
        final double absMotion = Math.abs(motionY);
        if(absMotion == 0.5 && gp.onLowBlock) maxMotion = isExempt(ExemptType.VELOCITY) ? 0.42: 0.5;
        final double finalMaxMotion = maxMotion + Math.abs(lastMotionY);

        if(absMotion > finalMaxMotion && !exempt) fail(packet, "Teleported vertically", "motionY §9" + absMotion + "§8/§9" + finalMaxMotion);

    }

}
