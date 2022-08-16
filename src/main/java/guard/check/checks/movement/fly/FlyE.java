package guard.check.checks.movement.fly;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.potion.PotionEffectType;

@GuardCheckInfo(name = "Fly E", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class FlyE extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.FLYING, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.VELOCITY, ExemptType.SLAB, ExemptType.STAIRS, ExemptType.NEAR_VEHICLE);
        if(motionY > .6 + (gp.player.hasPotionEffect(PotionEffectType.JUMP) ? ((gp.getPotionEffectAmplifier(PotionEffectType.JUMP) * 0.1) + 1) : 0) && !exempt) fail(packet, "Teleported upwards", "mY=" + motionY); else removeBuffer();
    }

}
