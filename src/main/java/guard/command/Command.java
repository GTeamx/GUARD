package guard.command;

import guard.Guard;
import guard.check.Check;
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

                        Guard.instance.configUtils.reloadConfigs();
                        GuardPlayerManager.clearGuardPlayers();
                        for(Player p : Bukkit.getOnlinePlayers()) {
                            GuardPlayerManager.addGuardPlayer(p);
                        }
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
                                for(Check c : gp.getCheckManager().checks) {
                                    String checkname = c.name.replace(" ", "");
                                    if (strings[1].equalsIgnoreCase(checkname)) {
                                        if(c.debugToPlayers.isEmpty()) {
                                            if(!c.isDebugging) {
                                                c.isDebugging = true;

                                            }
                                            c.debugToPlayers.add(sender);
                                            sender.sendMessage("§9§LGUARD §7»§f §aEnabled§f §fdebug for " + c.name + "§f!");
                                        } else {
                                            if(c.debugToPlayers.contains(sender)) {
                                                c.debugToPlayers.remove(sender);
                                                sender.sendMessage("§9§LGUARD §7»§f §cDisabled§f §fdebug for " + c.name + "§f!");
                                            } else {
                                                c.debugToPlayers.add(sender);
                                                sender.sendMessage("§9§LGUARD §7»§f §aEnabled§f §fdebug for " + c.name + "§f!");
                                            }
                                            if(c.debugToPlayers.isEmpty()) {
                                                c.isDebugging = false;
                                            }
                                        }
                                    }
                                }
                            } else {
                                Player target = Bukkit.getPlayer(strings[2]);
                                if(target != null) {
                                    GuardPlayer gp = GuardPlayerManager.getGuardPlayer(target);
                                    for (Check c : gp.getCheckManager().checks) {
                                        String checkname = c.name.replace(" ", "");
                                        if (strings[1].equalsIgnoreCase(checkname)) {
                                            if(c.debugToPlayers.isEmpty()) {
                                                 if(!c.isDebugging) {
                                                    c.isDebugging = true;

                                                }
                                                c.debugToPlayers.add(sender);
                                                sender.sendMessage("§9§LGUARD §7»§f §aEnabled§f §fdebugging " + c.name + " for §a" + target.getName() + "!");
                                            } else {
                                                if(c.debugToPlayers.contains(sender)) {
                                                    c.debugToPlayers.remove(sender);
                                                    sender.sendMessage("§9§LGUARD §7»§f §cDisabled§f §fdebugging " + c.name + " for §a" + target.getName() + "!");
                                                } else {
                                                    c.debugToPlayers.add(sender);
                                                    sender.sendMessage("§9§LGUARD §7»§f §aEnabled§f §fdebugging " + c.name + " for §a" + target.getName() + "!");
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
