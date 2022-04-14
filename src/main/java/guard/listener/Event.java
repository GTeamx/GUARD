package guard.listener;

import guard.Guard;
import guard.data.Data;
import guard.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Event implements Listener {



    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Guard.instance, () -> {
            Data.data.registerUser(e.getPlayer());
            PlayerData data = Data.data.getUserData(e.getPlayer());
            if (e.getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN) {
                if (System.currentTimeMillis() - data.joined < 800) {
                    data.tpafterjoin = System.currentTimeMillis();
                }
                if (System.currentTimeMillis() - data.wasDead < 300) {
                    data.tpafterded = System.currentTimeMillis();
                }
                data.lastTeleport = System.currentTimeMillis();

            } else {
                if (e.getTo().distanceSquared(e.getFrom()) < 0.05) {
                    data.weirdTeleport = System.currentTimeMillis();
                }
            }
        });
    }



    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Guard.instance, () -> {
            Data.data.registerUser(e.getEntity());
            PlayerData data = Data.data.getUserData(e.getEntity());
            data.isDead = true;
            data.wasDead = System.currentTimeMillis();
        });
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Guard.instance, () -> {
            Data.data.registerUser(e.getPlayer());
            PlayerData data = Data.data.getUserData(e.getPlayer());
            data.wasDead = System.currentTimeMillis();
        });
    }


    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Guard.instance, () -> {
            Data.data.registerUser(e.getPlayer());
            PlayerData data = Data.data.getUserData(e.getPlayer());
            data.lastBlockplaced = System.currentTimeMillis();
            data.blockplaced = e.getBlock();
        });
    }

    @EventHandler
    public void onDMG(EntityDamageEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Guard.instance, () -> {
            if (e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                Data.data.registerUser(p);
                PlayerData data = Data.data.getUserData(p);
                if(e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK || e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
                    data.validVelocityHit = true;
                else
                    data.validVelocityHit = false;
                data.lasthurt = System.currentTimeMillis();
                if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
                    data.lasthurtother = System.currentTimeMillis();
            }

        });
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Guard.instance, () -> {
            Data.data.registerUserJoin(e.getPlayer());
            PlayerData data = Data.data.getUserData(e.getPlayer());
            data.joined = System.currentTimeMillis();
            data.join = 0;
        });

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onbreak(BlockBreakEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Guard.instance, () -> {
            Data.data.registerUser(e.getPlayer());
            PlayerData data = Data.data.getUserData(e.getPlayer());
            data.brokenblock = e.getBlock();
            if(e.isCancelled()) {
                data.cancel = 0;
            }
        });

    }
}
