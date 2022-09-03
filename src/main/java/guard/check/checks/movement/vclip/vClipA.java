package guard.check.checks.movement.vclip;

import guard.Guard;
import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;

@GuardCheckInfo(name = "vClip A", category = GuardCategory.Movement, state = GuardCheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class vClipA extends GuardCheck {

    @Override
    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        final boolean exempt = isExempt(ExemptType.GLIDE, ExemptType.NEAR_VEHICLE, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.FLYING, ExemptType.FULL_LIQUID);

        double maxMotion = isExempt(ExemptType.VELOCITY) ? 1.5: 0.5;
        maxMotion += (double)(gp.hasPotionEffect(PotionEffectType.JUMP) ? (gp.getEffectByType(PotionEffectType.JUMP).get().getAmplifier() + 1) * 0.1F : 0);
        final double absMotion = Math.abs(motionY);
        final double finalMaxMotion = maxMotion + Math.abs(lastMotionY);

        if(absMotion > finalMaxMotion && !exempt) fail(packet, "Teleported vertically", "mY §9" + absMotion + "§8/§9" + finalMaxMotion);

    }
}
