package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Aim B", category = GuardCategory.Combat, state = GuardCheckState.STABLE, addBuffer = 1, removeBuffer = 1, maxBuffer = 2)
public class AimB extends GuardCheck {

    double lastModuloGCD3;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        if(Math.abs(deltaPitch) > 0.8 && lastDeltaPitch != deltaPitch) {
            final double moduloGCD = lastDeltaPitch % gp.realGCD;
            final double moduloGCD2 = deltaPitch % gp.realGCD;
            final double moduloGCD3 = gp.to.clone().getPitch() % gp.getGCD(Math.abs(deltaPitch), Math.abs(lastDeltaPitch));
            final double diff = Math.abs(moduloGCD3 - moduloGCD);
            final double justFuckingWork = moduloGCD3 % moduloGCD;
            //
            final double diffFuckingWork = Math.abs(moduloGCD3 - justFuckingWork);

            if(diff < 0.12 && justFuckingWork < 0.005 && justFuckingWork > -0.005 && String.valueOf(justFuckingWork).contains("E") && diffFuckingWork != 0 && diff > 0.008 && Math.abs(lastModuloGCD3 - moduloGCD3) < 0.002) {
                   buffer++;
                   if (buffer > 2) {
                       debug("§fmoduloGCD=" + moduloGCD + "\n §amoduloGCD3=" + moduloGCD3 + "\n §3diff=" + diff + "\n§ajustFuckingWork=" + justFuckingWork + "\n§3diffFuckingWork=" + diffFuckingWork + "\n §fb=" + buffer);
                       debug("§cFLAGERINO!!!!");
                   }
            } else {
                if(buffer > 0) {
                    buffer -= 0.25;
                }
            }
            debug("Buffer=" + buffer);
            if(Math.abs(moduloGCD3) < 0.09 && !String.valueOf(moduloGCD3).contains("E") && !String.valueOf(diff).contains("E") && diff < 0.004) {

            }
            lastModuloGCD3 = moduloGCD3;
            if(String.valueOf(moduloGCD).contains("E-4") && moduloGCD > 0 && !gp.isCinematic && Math.abs(moduloGCD2) < 0.0008) fail(null, "Impossible modulo GCD", "mGCD §9" + moduloGCD + "\n" + " §8»§f mGCD2 §9" + moduloGCD2);
            //else removeBuffer();
        }

    }
}