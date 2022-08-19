package guard.check.checks.combat.autoblock;

import guard.check.GuardCategory;
import guard.check.GuardCheck;
import guard.check.GuardCheckInfo;
import guard.check.GuardCheckState;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import org.bukkit.Material;

@GuardCheckInfo(name = "AutoBlock A", category = GuardCategory.Combat, state = GuardCheckState.Testing, addBuffer = 0, removeBuffer = 0, maxBuffer = 0)
public class AutoBlockA extends GuardCheck {

    double lastUse;

    @Override
    public void onPacket(PacketPlayReceiveEvent packet) {
        if(packet.getPacketId() == PacketType.Play.Client.USE_ENTITY) {
            WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(packet.getNMSPacket());
            debug("delay=" + (System.currentTimeMillis() - lastUse));
            if((System.currentTimeMillis() - lastUse < 86) && wrapper.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) fail(packet, "Invalid packet order", "lUd=" + (System.currentTimeMillis() - lastUse));
        } else if(packet.getPacketId() == PacketType.Play.Client.BLOCK_PLACE && packet.getPlayer().getInventory().getItemInMainHand().getType() == Material.WOODEN_SWORD || packet.getPlayer().getInventory().getItemInMainHand().getType() == Material.STONE_SWORD || packet.getPlayer().getInventory().getItemInMainHand().getType() == Material.GOLDEN_SWORD || packet.getPlayer().getInventory().getItemInMainHand().getType() == Material.IRON_SWORD ||packet.getPlayer().getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD || packet.getPlayer().getInventory().getItemInMainHand().getType() == Material.NETHERITE_SWORD) lastUse = System.currentTimeMillis();
    }
}
