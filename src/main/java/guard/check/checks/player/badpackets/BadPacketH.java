package guard.check.checks.player.badpackets;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import guard.exempt.ExemptType;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;

@GuardCheckInfo(name = "BadPacket H", category = GuardCategory.Player, state = GuardCheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class BadPacketH extends GuardCheck {
    private int count = 0;
    private WrappedPacketInEntityAction.PlayerAction lastAction;

    public void onPacket(PacketPlayReceiveEvent packet) {
        if (packet.getPacketId() == PacketType.Play.Client.ENTITY_ACTION) {
            boolean exempt = isExempt(ExemptType.RESPAWN, ExemptType.TELEPORT);
            WrappedPacketInEntityAction p = new WrappedPacketInEntityAction(packet.getNMSPacket());
            boolean isBedrock = PacketEvents.get().getPlayerUtils().isGeyserPlayer(gp.player) || gp.player.getName().contains(".");
            final boolean invalid = ++count > 1 && p.getAction() == lastAction;
            if (invalid && !exempt && !isBedrock) fail(packet, "Invalid action", "§9ENTITY_ACTION"); else removeBuffer();
            this.lastAction = p.getAction();
        } else if (packet.getPacketId() == PacketType.Play.Client.FLYING) count = 0;
    }

}
