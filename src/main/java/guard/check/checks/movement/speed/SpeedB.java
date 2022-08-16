package guard.check.checks.movement.speed;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.potion.PotionEffectType;

@GuardCheckInfo(name = "Speed B", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class SpeedB extends GuardCheck {
    int invalidA;
    double maxSpeed;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {
        boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.JOINED, ExemptType.FLYING, ExemptType.INSIDE_VEHICLE);
        boolean step = mathOnGround(motionY) && mathOnGround(gp.from.getY());
        if(gp.playerGround) invalidA++;
        if(!gp.playerGround) invalidA = 0;
        if(invalidA >= 8) maxSpeed = 0.2895 + (gp.getPotionEffectAmplifier(PotionEffectType.SPEED) > 0 ? (gp.getPotionEffectAmplifier(PotionEffectType.SPEED) * 0.0573) : 0);;
        if(invalidA < 8) maxSpeed = 0.62 + (gp.getPotionEffectAmplifier(PotionEffectType.SPEED) > 0 ? (gp.getPotionEffectAmplifier(PotionEffectType.SPEED) * .02313 + 0.2) : 0);
        if(step && (isExempt(ExemptType.STAIRS) || isExempt(ExemptType.SLAB))) maxSpeed += 0.2;
        if(isExempt(ExemptType.VELOCITY)) {
            maxSpeed += gp.kbLevel;
            maxSpeed += 0.45;
        }
        if(System.currentTimeMillis() - gp.lastIce < 1800) maxSpeed += 0.35;
        if(isExempt(ExemptType.SLIME)) maxSpeed += 0.3;
        if(isExempt(ExemptType.BLOCK_ABOVE))
            maxSpeed += 0.08;
        if(gp.getDeltaXZ() >= maxSpeed && !exempt) fail(packet, "Teleported horizontally", "cS=" + gp.getDeltaXZ() + " mS=" + maxSpeed); else removeBuffer();
    }

}
