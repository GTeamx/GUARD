package guard.check.checks.movement.motion;

import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Motion C", category = Category.MOVEMENT)
public class MotionC extends Check {


    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {
        boolean exempt = isExempt(ExemptType.FLYING, ExemptType.SLIME, ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.VELOCITY, ExemptType.SLAB, ExemptType.STAIRS);
        if(motionY > .5634 + (data.player.hasPotionEffect(PotionEffectType.JUMP) ? ((data.player.getPotionEffect(PotionEffectType.JUMP).getAmplifier() + 1) * 0.1) : 0) && !exempt) {
            fail("Teleported upwards", motionY);
        }
    }
}
