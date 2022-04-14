package guard.command;

import guard.Guard;
import guard.check.Check;
import guard.data.Data;
import guard.data.PlayerData;
import io.github.retrooper.packetevents.PacketEvents;
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
                        Data.data.clearDataBase();
                        return true;
                    } else if (strings[0].equalsIgnoreCase("alerts")) {
                        //Bukkit.broadcastMessage("§cReloading Guard");

                        PlayerData data = Data.data.getUserData(sender);
                        data.alertstoggled = !data.alertstoggled;
                        if (data.alertstoggled) {
                            sender.sendMessage("§3§lGuard §7»§f §fAlert output §aenabled§f!");
                        } else {
                            sender.sendMessage("§3§lGuard §7»§f §fAlert output §cdisabled§f!");
                        }
                        //sender.sendMessage("§a Guard!");
                        //Data.data.clearDataBase();
                        return true;
                    } else if (strings[0].equalsIgnoreCase("debug")) {
                        if(strings.length > 1) {
                            if(strings.length < 3) {
                                PlayerData data = Data.data.getUserData(sender);
                                for(Check c : data.checks) {
                                    String checkname = c.name.replace(" ", "");
                                    if (strings[1].equalsIgnoreCase(checkname)) {
                                        c.isdebugging = !c.isdebugging;
                                        if (c.isdebugging) {
                                            sender.sendMessage("§3§lGuard §7»§f §fDebugging output §aenabled§f for §a" + c.name + "!");
                                        } else {
                                            sender.sendMessage("§3§lGuard §7»§f §fDebugging output §cdisabled§f for §a" + c.name + "!");
                                        }
                                    }
                                }
                            } else {
                                Player target = Bukkit.getPlayer(strings[2]);
                                if(target != null) {
                                    PlayerData data = Data.data.getUserData(target);
                                    for (Check c : data.checks) {
                                        String checkname = c.name.replace(" ", "");
                                        if (strings[1].equalsIgnoreCase(checkname)) {
                                            c.isdebugging = !c.isdebugging;
                                            if (c.isdebugging) {
                                                c.debugtoplayers.add(sender);
                                                sender.sendMessage("§3§lGuard §7»§f §fDebugging output §aenabled§f for §a" + c.name + "!");
                                            } else {
                                                c.debugtoplayers.remove(sender);
                                                sender.sendMessage("§3§lGuard §7»§f §fDebugging output §cdisabled§f for §a" + c.name + "!");
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
