package guard.check.checks.combat.aim.aimassist;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.utils.BoundingBox;
import guard.utils.SampleList;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import org.bukkit.util.Vector;

import java.util.Collection;

@GuardCheckInfo(name = "AimAssist B", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 1, maxBuffer = 1)
public class AimAssistB extends GuardCheck {

    SampleList<Integer> yaws = new SampleList<>(2);
    float lastDiff;
    float lastDiff2;
    double lastAccel;
    double lastAccelAccel;
    double tempBuffer;



    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        /**if(Math.abs(deltaYaw) > 0) {
            float shiftedyaw = (deltaYaw * 100000000);
            int ysad = (int) shiftedyaw;
            yaws.add(Math.abs(ysad));
            if (yaws.isCollected())
                debug("dY=" + deltaYaw + " dP=" + deltaPitch + " yawsMode=" + (yaws.getMode(yaws) / 100000000));
        } **/
        /**if(lastDeltaPitch != deltaPitch && Math.abs(deltaPitch) > 1.5F && !gp.isCinematic) {
            double yaw = gp.wrapAngleTo180_float(gp.to.clone().getYaw()) % gp.sensitivity / 200F;
            double pitch = gp.to.clone().getPitch() % gp.sensitivity / 200F;
            double yawPitch = Math.abs(yaw - pitch);
            double diff = Math.abs(gp.getGCD(Math.abs(deltaPitch), Math.abs(lastDeltaPitch)) - yawPitch);
            if((diff < 0.015 || String.valueOf(diff).contains("E")) && diff > 0.007 ) {
                buffer++;
                if(buffer > 2) {
                    debug("§cFLAGERINO...:!:!::!:!");
                }
            } else {
                if(buffer > 0) {
                    buffer -= 0.1;
                }
            }
            //debug("yaw=" + yaw + " pitch=" + pitch + " realYaw=" + gp.wrapAngleTo180_float(gp.to.clone().getYaw()) + " realPitch=" + gp.to.clone().getPitch() + " GCD=" + gp.getGCD(Math.abs(deltaPitch), Math.abs(lastDeltaPitch)));
            debug("diff=" + diff);
        } **/
        if(Math.abs(deltaYaw) > 0) {
            float yaw = gp.wrapAngleTo180_float(gp.to.clone().getYaw());
            yaw = -yaw % gp.realGCD;
            float real = Math.abs(gp.wrapAngleTo180_float(gp.to.clone().getYaw()));
            float diffReal = Math.abs(real - lastDiff);
            float diffYaw = Math.abs(yaw - lastDiff2);
            if (real == lastDiff && yaw == lastDiff2) fail(null, "Sexo Dectado", "idkfuckyou");
            else removeBuffer();


            //debug("buffer=" + buffer + " diffReal=" + diffReal + " diffYaw=" + diffYaw + " accel=" + accel);

            lastDiff = real;
            lastDiff2 = yaw;
        }

    }

    public int getMode(Collection<? extends Number> list) {
        int mode = (int) list.toArray()[0];
        int maxCount = 0;
        for (Number value : list) {
            int count = 1;
            for (Number value2 : list) {
                if (value2.equals(value))
                    count++;
                if (count > maxCount) {
                    mode = (int) value;
                    maxCount = count;
                }
            }
        }
        return mode;
    }

}
