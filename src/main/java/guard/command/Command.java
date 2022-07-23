package guard.command;

import guard.Guard;
import guard.check.GuardCheck;
import guard.data.GuardPlayer;
import guard.data.GuardPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Command implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s,String[] strings) {
        if(commandSender instanceof Player) {
            Player sender = (Player)commandSender;
            if(sender.hasPermission("guard.reload") || sender.hasPermission("guard.*") || sender.isOp()) {
                if (command.getName().equalsIgnoreCase("guard")) {
                    if (strings[0].equalsIgnoreCase("reload")) {
                        //Bukkit.broadcastMessage("§cReloading Guard");
                        sender.sendMessage("§cReloading Guard!");
                        // PacketEvents.get().getInjector().eject();
                        //PacketEvents.get().getInjector().inject();
                        // for(Player p : Bukkit.getOnlinePlayers()) {
                        //     PacketEvents.get().getInjector().injectPlayer(p);
                        // }
                        Guard.instance.configUtils.reloadConfigs();
                        GuardPlayerManager.clearGuardPlayers();
                        return true;
                    } else if (strings[0].equalsIgnoreCase("alerts")) {
                        //Bukkit.broadcastMessage("§cReloading Guard");

                        GuardPlayer gp = GuardPlayerManager.getGuardPlayer(sender);
                        gp.alertsToggled = !gp.alertsToggled;
                        if (gp.alertsToggled) {
                            sender.sendMessage("§9§lGUARD §7»§f §fAlert output §aenabled§f!");
                        } else {
                            sender.sendMessage("§9§lGUARD §7»§f §fAlert output §cdisabled§f!");
                        }
                        //sender.sendMessage("§a Guard!");
                        //Data.data.clearDataBase();
                        return true;
                    } else if (strings[0].equalsIgnoreCase("debug")) {
                        if(strings.length > 1) {
                            if(strings.length < 3) {
                                GuardPlayer gp = GuardPlayerManager.getGuardPlayer(sender);
                                for(GuardCheck c : gp.getCheckManager().checks) {
                                    String checkname = c.name.replace(" ", "");
                                    if (strings[1].equalsIgnoreCase(checkname)) {
                                        c.isDebugging = !c.isDebugging;
                                        if (c.isDebugging) {
                                            sender.sendMessage("§9§lGUARD §7»§f §fDebugging output §aenabled§f for §a" + c.name + "!");
                                        } else {
                                            sender.sendMessage("§9§lGUARD §7»§f §fDebugging output §cdisabled§f for §a" + c.name + "!");
                                        }
                                    }
                                }
                            } else {
                                Player target = Bukkit.getPlayer(strings[2]);
                                if(target != null) {
                                    GuardPlayer gp = GuardPlayerManager.getGuardPlayer(sender);
                                    for (GuardCheck c : gp.getCheckManager().checks) {
                                        String checkname = c.name.replace(" ", "");
                                        if (strings[1].equalsIgnoreCase(checkname)) {
                                            if(c.debugToPlayers.isEmpty()) {
                                                 if(!c.isDebugging) {
                                                    c.isDebugging = true;

                                                }
                                                c.debugToPlayers.add(sender);
                                                sender.sendMessage("§9§LGUARD §7»§f §aEnabled§f §fdebugging " + c.name + "for §a" + target.getName() + "!");
                                            } else {
                                                if(c.debugToPlayers.contains(sender)) {
                                                    c.debugToPlayers.remove(sender);
                                                    sender.sendMessage("§9§LGUARD §7»§f §cDisabled§f §fdebugging " + c.name + "for §a" + target.getName() + "!");
                                                }
                                                if(c.debugToPlayers.isEmpty()) {
                                                    c.isDebugging = false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return true;

                    }
                }
            }
            else {
                sender.sendMessage("Unknown command. Type \"/help\" for help.");
            }
        }

        return false;
    }
}
