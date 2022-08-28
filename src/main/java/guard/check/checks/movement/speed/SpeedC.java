package guard.check.checks.movement.speed;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.DoubleSummaryStatistics;

@GuardCheckInfo(name = "Speed C", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 1, maxBuffer = 6)
public class SpeedC extends GuardCheck {

    double maxSpeed = 1;
    double cSpeed = 0;
    int groundTicks = 0;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.FLYING, ExemptType.TELEPORT);
        cSpeed = gp.getDistance(true);

        if(!gp.inAir) groundTicks++;
        else groundTicks = 0;

        // BASIC | Ground - Air
        if(groundTicks > 12) {
            maxSpeed = 0.2868198;
        } else maxSpeed = 0.3358;

        // BASIC | HitHead
        if(System.currentTimeMillis() - gp.lastBlockAbove < 1200) maxSpeed = 0.5;

        // ICE | Ground - Air
        if(System.currentTimeMillis() - gp.lastIce <  1200) {
            if (groundTicks > 22) {
                if(System.currentTimeMillis() - gp.lastIce < 50) maxSpeed = 0.2757;
            } else {
                maxSpeed = 0.48;
                if(System.currentTimeMillis() - gp.lastBlockAbove < 1200) maxSpeed = 0.9;
            }
        }

        // SLIME | Ground - Air
        if(isExempt(ExemptType.SLIME)) {
            if(groundTicks > 14) {
                if(System.currentTimeMillis() - gp.lastSlime < 50) maxSpeed = 0.09;
            } else {
                maxSpeed = 0.45;
                if(System.currentTimeMillis() - gp.lastBlockAbove < 1200) maxSpeed = 0.74;
            }
        }

        // COBWEB | Ground - Air
        if(isExempt(ExemptType.WEB)) {
            if(groundTicks > 2) {
                maxSpeed = 0.1;
            } else {
                maxSpeed = 0.1004;
                if(gp.blockAbove) maxSpeed = 0.108;
            }
        }

        // SOUL SAND | Ground
        // TODO: Support soul sand + soul speed enchantment

        // SPEED | Ground - Air
        if(gp.player.hasPotionEffect(PotionEffectType.SPEED)) {
            if(groundTicks > 2) {
                maxSpeed += (gp.getPotionEffectAmplifier(PotionEffectType.SPEED) * .0573);
            } else {
                maxSpeed += (gp.getPotionEffectAmplifier(PotionEffectType.SPEED) * .02313);
            }
        }

        // WATER | Ground - Air
        if(gp.isInLiquid && !gp.playerGround) {
            // TODO: Add support for tridents and their enchantments
            maxSpeed = 0.29;
            if(gp.getDepthStriderLevel() > 0) {
                if(gp.getDepthStriderLevel() == 1) maxSpeed += 0.03;
                if(gp.getDepthStriderLevel() == 2) maxSpeed += 0.05;
                if(gp.getDepthStriderLevel() == 3) maxSpeed += 0.07;
                if(gp.getDepthStriderLevel() > 3) maxSpeed = (0.08 * gp.getDepthStriderLevel());
            }
        }

        // STAIRS/SLABS | Ground - Air
        if(isExempt(ExemptType.STAIRS) || isExempt(ExemptType.SLAB)) maxSpeed = 0.35;

        // DAMAGE | Air
        if(isExempt(ExemptType.VELOCITY)) {
            maxSpeed = 0.8;
            if(gp.kbLevel > 0) {
                maxSpeed = (gp.kbLevel * 0.95);
            }
        }

        // /SPEED | Ground - Air
        //if(gp.getPlayer().getWalkSpeed() > 0.2) maxSpeed += (gp.getPlayer().getWalkSpeed());

        debug("cs=" + cSpeed + " ms="+ maxSpeed + " b=" + buffer);
        if(cSpeed > maxSpeed  && !exempt) fail(packet, "Moving too fast", "cS=" + cSpeed + "mS=" + maxSpeed);
        if(cSpeed <= maxSpeed) removeBuffer();
    }

}
