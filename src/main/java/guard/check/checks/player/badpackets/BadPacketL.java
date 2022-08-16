package guard.check.checks.player.badpackets;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import org.bukkit.GameMode;

@GuardCheckInfo(name = "BadPacket L", category = GuardCategory.Player, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class BadPacketL extends GuardCheck {

    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.SPECTATE) {
            if(packet.getPlayer().getGameMode() != GameMode.SPECTATOR) fail(packet, "Impossible packet", "SPECTATE"); else removeBuffer();
        }
    }

}
