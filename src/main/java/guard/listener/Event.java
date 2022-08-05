package guard.listener;

import guard.Guard;
import guard.data.GuardPlayer;
import guard.data.GuardPlayerManager;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Event implements Listener {



    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Guard.instance, () -> {
            GuardPlayerManager.addGuardPlayer(e.getPlayer());
            GuardPlayer gp = GuardPlayerManager.getGuardPlayer(e.getPlayer());
            if (e.getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN) {
                if (System.currentTimeMillis() - gp.joined < 800) {
                    gp.tpAfterJoin = System.currentTimeMillis();
                }
                gp.lastTeleport = System.currentTimeMillis();

            } else {
                if (e.getTo().distanceSquared(e.getFrom()) < 0.05) {
                    gp.weirdTeleport = System.currentTimeMillis();
                }
            }
        });
    }



    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Guard.instance, () -> {
            GuardPlayerManager.addGuardPlayer(e.getEntity());
            GuardPlayer gp = GuardPlayerManager.getGuardPlayer(e.getEntity());
            gp.isDead = true;
            gp.wasDead = System.currentTimeMillis();
        });
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Guard.instance, () -> {
            GuardPlayerManager.addGuardPlayer(e.getPlayer());
            GuardPlayer gp = GuardPlayerManager.getGuardPlayer(e.getPlayer());
            gp.wasDead = System.currentTimeMillis();
        });
    }


    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Guard.instance, () -> {
            GuardPlayerManager.addGuardPlayer(e.getPlayer());
            GuardPlayer gp = GuardPlayerManager.getGuardPlayer(e.getPlayer());
            gp.lastBlockPlaced = System.currentTimeMillis();
            gp.blockPlaced = e.getBlock();
        });
    }


    @EventHandler
    public void onDMG(EntityDamageEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Guard.instance, () -> {
            if (e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                GuardPlayerManager.addGuardPlayer(p);
                GuardPlayer gp = GuardPlayerManager.getGuardPlayer(p);
                if(e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
                    gp.validVelocityHit = true;
                else if(PacketEvents.get().getServerUtils().getVersion().isNewerThanOrEquals(ServerVersion.v_1_9)) {
                    gp.validVelocityHit = e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK;
                } else {
                    gp.validVelocityHit = false;
                }

                gp.lastHurt = System.currentTimeMillis();
                if(e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                    gp.entityHit = System.currentTimeMillis();

                if(PacketEvents.get().getServerUtils().getVersion().isNewerThanOrEquals(ServerVersion.v_1_9)) {
                    if(e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
                        gp.entityHit = System.currentTimeMillis();
                    }
                }


                if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
                    gp.lastHurtOther = System.currentTimeMillis();
            }

        });
    }

    @EventHandler
    public void onEntityDMG(EntityDamageByEntityEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Guard.instance, () -> {
            if(e.getDamager() instanceof Player) {
                GuardPlayer temp = GuardPlayerManager.getGuardPlayer((Player) e.getDamager());
                temp.lastAttack = System.currentTimeMillis();
            }
            if (e.getEntity() instanceof Player) {
                Player p = (Player) e.getEntity();
                GuardPlayerManager.addGuardPlayer(p);
                GuardPlayer gp = GuardPlayerManager.getGuardPlayer(p);
                if(e.getDamager() instanceof Player || e.getDamager() instanceof Mob) {
                    if(!((LivingEntity) e.getDamager()).getEquipment().getItemInMainHand().getType().equals(Material.AIR)) {
                        if (((LivingEntity) e.getDamager()).getEquipment().getItemInMainHand().containsEnchantment(Enchantment.KNOCKBACK))
                            gp.kbLevel = ((LivingEntity) e.getDamager()).getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.KNOCKBACK);

                        if (((LivingEntity) e.getDamager()).getEquipment().getItemInMainHand().containsEnchantment(Enchantment.ARROW_KNOCKBACK))
                            gp.kbLevel = ((LivingEntity) e.getDamager()).getEquipment().getItemInMainHand().getEnchantmentLevel(Enchantment.ARROW_KNOCKBACK);
                    }
                }
            }

        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Guard.instance, () -> {
            GuardPlayerManager.addGuardPlayerJoin(e.getPlayer());
            GuardPlayer gp = GuardPlayerManager.getGuardPlayer(e.getPlayer());
            gp.joined = System.currentTimeMillis();
            gp.join = 0;
        });

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Guard.instance, () -> {
            GuardPlayerManager.addGuardPlayer(e.getPlayer());
            GuardPlayerManager.removeGuardPlayer(e.getPlayer());
        });

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(Guard.instance, () -> {
            GuardPlayerManager.addGuardPlayer(e.getPlayer());
            GuardPlayer gp = GuardPlayerManager.getGuardPlayer(e.getPlayer());
            gp.brokenBlock = e.getBlock();
        });

    }
}
