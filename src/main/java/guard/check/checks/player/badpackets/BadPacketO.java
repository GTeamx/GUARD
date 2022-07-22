package guard.check.checks.player.badpackets;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;

@GuardCheckInfo(name = "BadPacket O", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class BadPacketO extends GuardCheck {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.LOOK || packet.getPacketId() == PacketType.Play.Client.POSITION_LOOK) {
            WrappedPacketInFlying wrapper = new WrappedPacketInFlying(packet.getNMSPacket());
            if(wrapper.getPitch() > 90 || wrapper.getPitch() < -90) fail(packet, "Impossible rotations", "pitch=" + wrapper.getPitch());
        }
    }

}
