package guard.check.checks.combat.autoblock;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.Material;

@GuardCheckInfo(name = "AutoBlock A", category = GuardCategory.Combat, state = GuardCheckState.EXPERIMENTAL, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class AutoBlockA extends GuardCheck {

    double lastUse;

    @Override
    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getNMSPacket());
            debug("delay=" + (System.currentTimeMillis() - lastUse));
            if((System.currentTimeMillis() - lastUse < 86) && wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) fail(packet, "Impossible packet delay", "lU §9" + (System.currentTimeMillis() - lastUse));
        } else if(packet.getPacketId() == PacketType.Play.Client.BLOCK_PLACE && packet.getPlayer().getInventory().getItemInMainHand().getType().toString().contains("SWORD")) lastUse = System.currentTimeMillis();
    }
}
