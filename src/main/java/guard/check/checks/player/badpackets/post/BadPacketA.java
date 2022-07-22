package guard.check.checks.player.badpackets.post;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.armanimation.WrappedPacketInArmAnimation;

@GuardCheckInfo(name = "BadPacket A", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class BadPacketA extends GuardCheck {

    public void onPacket(PacketPlayReceiveEvent packet) {
        boolean isPost = isPost(packet.getPacketId(), PacketType.Play.Client.ARM_ANIMATION);
        WrappedPacketInArmAnimation arm = new WrappedPacketInArmAnimation(packet.getNMSPacket());
        if (isPost) fail(packet, "Post packet", "ARM_ANIMATION");
    }

}
