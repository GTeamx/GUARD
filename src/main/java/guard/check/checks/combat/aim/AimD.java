package guard.check.checks.combat.aim;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.out.position.WrappedPacketOutPosition;

@GuardCheckInfo(name = "Aim D", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0, maxBuffer = 4)
public class AimD extends GuardCheck {

    float currentPitch, previousPitch, currentYaw, previousYaw;

    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        if(deltaPitch > 0) {

            currentPitch = gp.getPlayer().getLocation().getPitch();

            if(deltaPitch == lastDeltaPitch) fail(packet, "Repeated Rotation", "PITCH");
            else removeBuffer();

            previousPitch = gp.getPlayer().getLocation().getPitch();

        }

        if(deltaYaw > 0) {

            currentYaw = gp.getPlayer().getLocation().getYaw();

            if(deltaYaw == lastDeltaYaw) fail(packet, "Repeated Rotation", "YAW");
            else removeBuffer();

            previousYaw = gp.getPlayer().getLocation().getYaw();

        }
    }
}
