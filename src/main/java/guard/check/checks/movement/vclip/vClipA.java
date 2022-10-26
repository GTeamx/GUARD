package guard.check.checks.movement.vclip;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.check.CheckState;
import guard.exempt.ExemptType;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "vClip A", category = Category.Movement, state = CheckState.STABLE)
public class vClipA extends Check {

    @Override
    public void onMove(PacketReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

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
