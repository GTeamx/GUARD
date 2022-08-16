package guard.check.checks.player.badpackets;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import guard.utils.packet.TransactionPacketClient;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;

@GuardCheckInfo(name = "BadPacket Q", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class BadPacketQ extends GuardCheck {

    boolean found;
    int moves;


    public void onMove(PacketPlayReceiveEvent packet, double motionX, double motionY, double motionZ, double lastMotionX, double lastMotionY, double lastMotionZ, float deltaYaw, float deltaPitch, float lastDeltaYaw, float lastDeltaPitch) {
        boolean exempt = isExempt(ExemptType.JOINED);
        if(!found && moves > 80 && !exempt) {
            fail(null, "Missing Transaction Packet", moves);
        }
        moves++;
        if(found) {
            moves = 0;
            found = false;
        }

    }

    public void onTransaction(TransactionPacketClient packet, boolean found) {
        if(found) this.found = true;
        debug("id=" + packet.getId() + " found=" + found);
    }
}
