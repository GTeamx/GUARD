package guard.check.checks.movement.fly;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "Fly F", category = GuardCategory.Movement, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class FlyF extends GuardCheck {

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.TELEPORT, ExemptType.SLIME, ExemptType.LIQUID, ExemptType.WEB, ExemptType.GROUND, ExemptType.JOINED, ExemptType.INSIDE_VEHICLE, ExemptType.FLYING);
        boolean step = mathOnGround(motionY) && mathOnGround(gp.from.getY());
        double predictionMotionY = (lastMotionY - 0.08D) * (double) 0.98F;
        boolean jumped = !gp.playerGround && gp.lastPlayerGround;
        if(jumped) {
            predictionMotionY = 0.42f;
        }
        debug("predicted=" + predictionMotionY + " step=" + step + " diff=" + Math.abs(motionY - predictionMotionY) + " jumped=" + jumped);
        if(!exempt && !step && Math.abs(motionY - predictionMotionY) > 1.0 && (System.currentTimeMillis() - gp.lastHurt > 300)) fail(packet, "Teleported downwards", "mY=" + Math.abs(motionY - predictionMotionY)); else removeBuffer();
    }

}
