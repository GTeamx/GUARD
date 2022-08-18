package guard.listener;

import guard.data.GuardPlayer;
import guard.data.GuardPlayerManager;
import io.github.retrooper.packetevents.PacketEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.nio.charset.StandardCharsets;

public final class ClientBrandListener implements PluginMessageListener, Listener {

    @Override
    public void onPluginMessageReceived(final String channel, final Player player, final byte[] msg) {
        try {
            GuardPlayer gp = GuardPlayerManager.getGuardPlayer(player);
            if (msg.length == 0) return;
            final String clientBrand = new String(msg, StandardCharsets.UTF_8).length() > 0 ? new String(msg, StandardCharsets.UTF_8).substring(1) : new String(msg, StandardCharsets.UTF_8);
            gp.clientBrand = clientBrand;
            for(Player p : Bukkit.getOnlinePlayers()) if(p.hasPermission("guard.joinalerts")) p.sendMessage("§9§lGUARD §7»§f " + player.getName() + " §7joined using §f" + clientBrand + " §7in §f" + PacketEvents.get().getPlayerUtils().getClientVersion(player).name().replaceAll("_", ".").substring(2));
        } catch (final Throwable t) {
            System.out.println("An error occurred with ClientBrandListener. You can ignore this.");
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        addChannel(event.getPlayer());
    }

    private void addChannel(final Player player) {
        try {
            player.getClass().getMethod("addChannel", String.class).invoke(player, "MC|BRAND");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
