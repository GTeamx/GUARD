package guard.check.checks.movement.speed;

import guard.check.Category;
import guard.check.Check;
import guard.check.CheckInfo;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@CheckInfo(name = "Speed B", category = Category.MOVEMENT)
public class SpeedB extends Check {

    double cSpeed = 0;
    double maxSpeed = 1;
    int groundTicks = 0;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastmotionX, double lastmotionY, double lastmotionZ, float deltaYaw, float deltaPitch, float lastdeltaYaw, float lastdeltaPitch) {

        boolean exempt = isExempt(ExemptType.INSIDE_VEHICLE, ExemptType.FLYING, ExemptType.TELEPORT);

        // GROUND TICKS CALCULATOR
        cSpeed = data.getDistance(true);
        if(data.playerGround) groundTicks++;
        if(!data.playerGround) groundTicks = 0;

        // AIR/GROUND LIMIT
        if(groundTicks >= 12) maxSpeed = 0.2868198;
        if(groundTicks < 12) maxSpeed = 0.338;

        // HIT HEAD
        if(data.blockabove) maxSpeed = 0.5;

        // ICE
        if(System.currentTimeMillis() - data.lastice <= 1200) {
            if(groundTicks >= 22 && System.currentTimeMillis() - data.lastice <= 50 && !data.blockabove) maxSpeed = 0.2757;
            if(groundTicks < 22) maxSpeed = 0.48;
            if(data.blockabove) maxSpeed = 0.9;
        }

        // SLIME
        if(System.currentTimeMillis() - data.lastslime <= 1200) {
            if(groundTicks >= 14 && System.currentTimeMillis() - data.lastslime <= 50 && !data.blockabove) maxSpeed = 0.09;
            if(groundTicks < 14) maxSpeed = 0.45;
            if(data.blockabove) maxSpeed = 0.74;
        }

        // COBWEB
        if(data.inweb) {
            if(groundTicks >= 2) maxSpeed = 0.1;
            if(groundTicks < 2) maxSpeed = 0.1004;
            if(data.blockabove) maxSpeed = 0.108;
        }

        // SPEED POTION / EFFECT HANDLING
        /* TODO: Add data.lastSpeed (Double variable with last movement with a speed effect). */

        // 1.13 SWIM
        /* TODO: Add data.isSwimming (Checking for 1.13 swimming in water). */

        if(System.currentTimeMillis() - data.lasthurt < 1200 || System.currentTimeMillis() - data.lasthurtother < 1200) maxSpeed = 0.7;
        if(System.currentTimeMillis() - data.lasthurt < 1500 || System.currentTimeMillis() - data.lasthurtother < 1500 && data.kblevel > 0) maxSpeed = data.kblevel * 0.95;

        if(cSpeed > maxSpeed && !exempt) fail("Speed Limit", "cSpeed=" + cSpeed + " maxSpeed=" + maxSpeed);
        if(cSpeed < maxSpeed && !exempt) removeBuffer();
    }
}
