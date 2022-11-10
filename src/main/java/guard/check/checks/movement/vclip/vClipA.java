package guard.check.checks.movement.vclip;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.check.CheckState;
import guard.exempt.ExemptType;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "vClip A", category = Category.Movement, state = CheckState.STABLE)
public class vClipA extends Check {

    private long lastJumpBoost = 15000L;

    @Override
    public void onMove(PacketReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        // TODO: Add WATER_SOUlSAND and WATER_MAGMA exempt types.

        final boolean exempt = isExempt(ExemptType.GLIDE, ExemptType.NEAR_VEHICLE, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.FLYING);

        // Proper handling for JumpBoost.
        if(gp.getPotionEffectAmplifier(PotionEffectType.JUMP) > 0) lastJumpBoost = System.currentTimeMillis();
        final float extraTicks = lastJumpBoost - System.currentTimeMillis() >= 1500 ? 0 : (gp.getPotionEffectAmplifier(PotionEffectType.JUMP) + 1) * 0.11F;

        final double absMotion = Math.abs(motionY);
        final double finalMaxMotion = extraTicks + Math.abs(lastMotionY);

        if(absMotion > finalMaxMotion && !exempt) fail(packet, "Teleported vertically", "motionY §9" + absMotion + "§8/§9" + finalMaxMotion);

    }

}
