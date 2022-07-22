package guard.check.checks.player.badpackets;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.abilities.WrappedPacketInAbilities;

@GuardCheckInfo(name = "BadPacket J", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class BadPacketJ extends GuardCheck {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.ABILITIES) {
            WrappedPacketInAbilities wrapper = new WrappedPacketInAbilities(packet.getNMSPacket());
            if(wrapper.isFlying() && !packet.getPlayer().isFlying()) fail(packet, "Impossible packet", "ABILITIES");
        }
    }

}
