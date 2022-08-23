package guard.check.checks.player.badpackets;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "BadPacket R", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 1, removeBuffer = 0.05, maxBuffer = 20)
public class BadPacketR extends GuardCheck {

    @Override
    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {

        if(gp.getPlayer().isSprinting() && gp.getPlayer().getFoodLevel() <= 6) fail(packet, "Impossible Sprint", "fL=" + gp.getPlayer().getFoodLevel());
        else removeBuffer();
        debug("fL=" + gp.getPlayer().getFoodLevel() + " sp=" + gp.getPlayer().isSprinting() + " b=" + buffer);
    }
}
