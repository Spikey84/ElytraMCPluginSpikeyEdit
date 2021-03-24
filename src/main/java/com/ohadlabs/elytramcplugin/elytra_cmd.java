// 
// Decompiled by Procyon v0.5.36
// 

package com.ohadlabs.elytramcplugin;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.PlayerInventory;
import java.util.Iterator;
import java.text.DecimalFormat;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public class elytra_cmd implements TabExecutor
{
    public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] strings) {
        if(strings.length == 0){
            commandSender.sendMessage("§eYou have to add arguments to your command!§r");
            return false;
        }
        if (!strings[0].equals("map_only_op")) {
            if (commandSender instanceof Player) {
                final Player player = (Player)commandSender;
                if (strings.length >= 2) {
                    if (strings[0].equals("start")) {
                        if (plugin_memory.player_playing(player) == -1) {
                            boolean map_exist = false;
                            boolean multimap_exist = false;
                            int map_idx = 0;
                            int multimap_idx = 0;
                            for (final map current_map : plugin_memory.maps) {
                                if (current_map.map_name.equals(strings[1])) {
                                    map_exist = true;
                                    break;
                                }
                                ++map_idx;
                            }
                            if (!map_exist && strings.length == 3 && strings[2].equals("multi")) {
                                for (final multimap current_map2 : plugin_memory.multimaps) {
                                    if (current_map2.multimap_name.equals(strings[1])) {
                                        multimap_exist = true;
                                        break;
                                    }
                                    ++multimap_idx;
                                }
                            }
                            if (map_exist || multimap_exist) {
                                final PlayerInventory inv = player.getInventory();
                                try {
                                    for (int i = 0; i < 36; ++i) {
                                        inv.getItem(i).getType().name();
                                    }
                                    player.sendMessage("§eInventory is full, please empty one spot!§r");
                                    return true;
                                }
                                catch (Exception ex) {
                                    player.getInventory().setItem(player.getInventory().firstEmpty(), player.getInventory().getItemInOffHand());
                                    if (map_exist) {
                                        plugin_memory.current_playing_players.add(new playing_player(player, map_idx, player.getLocation()));
                                    }
                                    else {
                                        plugin_memory.current_playing_players.add(new playing_player(player, plugin_memory.multimaps.get(multimap_idx).map_idxs.get(0), player.getLocation()));
                                    }
                                    plugin_memory.current_playing_players.get(plugin_memory.current_playing_players.size() - 1).inv = player.getInventory().getContents().clone();
                                    plugin_memory.current_playing_players.get(plugin_memory.current_playing_players.size() - 1).armor = player.getInventory().getArmorContents().clone();
                                    inv.setChestplate(new ItemStack(Material.ELYTRA));
                                    plugin_memory.current_playing_players.get(plugin_memory.current_playing_players.size() - 1).join_time = System.currentTimeMillis();
                                    plugin_memory.current_playing_players.get(plugin_memory.current_playing_players.size() - 1).gm = player.getGameMode();
                                    if (strings.length == 3 && (strings[2].equals("loop") || strings[2].equals("multi"))) {
                                        plugin_memory.current_playing_players.get(plugin_memory.current_playing_players.size() - 1).restart = true;
                                    }
                                    player.setGameMode(GameMode.CREATIVE);
                                    player.setAllowFlight(false);
                                    if (map_exist) {
                                        player.teleport(plugin_memory.maps.get(map_idx).start);
                                        Bukkit.broadcastMessage(player.getDisplayName() + " §astarted map:§r " + strings[1]);
                                    }
                                    else if (multimap_exist) {
                                        player.teleport(plugin_memory.maps.get(plugin_memory.multimaps.get(multimap_idx).map_idxs.get(0)).start);
                                        for (final int mm_idx : plugin_memory.multimaps.get(multimap_idx).map_idxs) {
                                            plugin_memory.current_playing_players.get(plugin_memory.current_playing_players.size() - 1).mm.add(mm_idx);
                                        }
                                        plugin_memory.current_playing_players.get(plugin_memory.current_playing_players.size() - 1).mm_idx = multimap_idx;
                                        Bukkit.broadcastMessage(player.getDisplayName() + " §astarted multimap:§r " + strings[1]);
                                    }
                                    return true;
                                }
                            }
                            player.sendMessage("§cInvalid map name!§r");
                        }
                        else {
                            player.sendMessage("§eYou are already playing!§r");
                        }
                    }
                    else if (strings[0].equals("loop")) {
                        player.performCommand("elytra start " + strings[1] + " loop");
                    }
                    else if (strings[0].equals("multistart")) {
                        player.performCommand("elytra start " + strings[1] + " multi");
                    }
                    else if (strings[0].equals("map")) {
                        if (plugin_memory.op_only && !player.isOp()) {
                            player.sendMessage("§eOnly op's are allowed to create maps. Sry for them damn nazi mods...§r");
                            return true;
                        }
                        if (plugin_memory.creating_map(player) == -1) {
                            for (final map _map : plugin_memory.maps) {
                                if (_map.map_name.equals(strings[1])) {
                                    player.sendMessage(_map.map_name + " §ealready exist, be creative man... You can figure out your own unique name... I trust you!§r");
                                    return true;
                                }
                            }
                            plugin_memory.maps.add(new map(strings[1]));
                            plugin_memory.map_creators.add(new creating_map(player, plugin_memory.maps.size() - 1));
                            player.sendMessage("§aInitialized map creation of map:§r " + strings[1]);
                        }
                        else {
                            player.sendMessage("§eAlready creating map!§r");
                        }
                    }
                    else if (strings[0].equals("delete")) {
                        for (int j = 0; j < plugin_memory.maps.size(); ++j) {
                            if (plugin_memory.maps.get(j).map_name.equals(strings[1])) {
                                for (int k = 0; k < plugin_memory.current_playing_players.size(); ++k) {
                                    if (plugin_memory.current_playing_players.get(k).map_idx == j) {
                                        player.sendMessage("§cMap can't be deleted while someone is playing it!");
                                        return true;
                                    }
                                }
                                Bukkit.broadcastMessage("Map: §c" + plugin_memory.maps.get(j).map_name + "§r was deleted!");
                                for (int k = 0; k < plugin_memory.multimaps.size(); ++k) {
                                    try {
                                        for (int l = 0; l < plugin_memory.multimaps.get(k).map_idxs.size(); ++l) {
                                            if (plugin_memory.multimaps.get(k).map_idxs.get(l) == j) {
                                                plugin_memory.multimaps.get(k).map_idxs.remove(l);
                                                Bukkit.broadcastMessage("This affected multimap: " + plugin_memory.multimaps.get(k).multimap_name);
                                                if (plugin_memory.multimaps.get(k).map_idxs.size() < 2) {
                                                    plugin_memory.multimaps.remove(k);
                                                }
                                            }
                                        }
                                    }
                                    catch (Exception ex2) {}
                                }
                                plugin_memory.maps.remove(j);
                                plugin_memory.save_maps();
                                return true;
                            }
                        }
                        for (int j = 0; j < plugin_memory.multimaps.size(); ++j) {
                            for (int k = 0; k < plugin_memory.current_playing_players.size(); ++k) {
                                if (plugin_memory.current_playing_players.get(k).mm_idx == j) {
                                    player.sendMessage("§cMap can't be deleted while someone is playing it!");
                                    return true;
                                }
                            }
                            if (plugin_memory.multimaps.get(j).multimap_name.equals(strings[1])) {
                                Bukkit.broadcastMessage("Multimap: §c" + plugin_memory.multimaps.get(j).multimap_name + "§r was deleted!");
                                plugin_memory.multimaps.remove(j);
                                plugin_memory.save_maps();
                                return true;
                            }
                        }
                        player.sendMessage("§eUnknown map name!§r");
                    }
                    else if (strings[0].equals("highscore")) {
                        for (int j = 0; j < plugin_memory.maps.size(); ++j) {
                            if (plugin_memory.maps.get(j).map_name.equals(strings[1])) {
                                player.sendMessage("§aHighscores for this map:§r");
                                for (int k = 0; k < plugin_memory.maps.get(j).highscore.size(); ++k) {
                                    final DecimalFormat df = new DecimalFormat("0.00");
                                    player.sendMessage(k + 1 + ". " + df.format(plugin_memory.maps.get(j).highscore.get(k).time / 1000.0) + " " + plugin_memory.maps.get(j).highscore.get(k).name);
                                }
                                return true;
                            }
                        }
                        player.sendMessage("§eUnknown map name!§r");
                    }
                    else if (strings[0].equals("multimap")) {
                        if (strings.length < 4) {
                            player.sendMessage("§eThere must atleast be 2 maps for a multimap!§r");
                        }
                        for (final map _map : plugin_memory.maps) {
                            if (_map.map_name.equals(strings[1])) {
                                player.sendMessage(_map.map_name + " §ealready exist, be creative man... You can figure out your own unique name... I trust you!§r");
                                return true;
                            }
                        }
                        for (final multimap _map2 : plugin_memory.multimaps) {
                            if (_map2.multimap_name.equals(strings[1])) {
                                player.sendMessage(_map2.multimap_name + " §ealready exist, be creative man... You can figure out your own unique name... I trust you!§r");
                                return true;
                            }
                        }
                        final multimap mult_map = new multimap(strings[1]);
                        int cnter = 0;
                        for (int m = 2; m < strings.length; ++m) {
                            boolean good = false;
                            for (int j2 = 0; j2 < plugin_memory.maps.size(); ++j2) {
                                if (plugin_memory.maps.get(j2).map_name.equals(strings[m])) {
                                    mult_map.map_idxs.add(j2);
                                    ++cnter;
                                    good = true;
                                }
                            }
                            if (!good) {
                                player.sendMessage(strings[m] + " §eIs not a map§r");
                            }
                        }
                        if (cnter < 2) {
                            player.sendMessage("§eThere must atleast be 2 maps for a multimap!§r");
                        }
                        else {
                            player.sendMessage("§aMultimap added!§r");
                            plugin_memory.multimaps.add(mult_map);
                            plugin_memory.save_maps();
                        }
                    }
                    else {
                        player.sendMessage("§eUnknown arguments, please type something like §a'/elytra start easy'§e to start a map with name easy. No guarantee that a map named easy actually exist though.§r First argument was: " + strings[0]);
                    }
                }
                else if (strings.length == 1) {
                    if (strings[0].equals("end")) {
                        final int map_idx2 = plugin_memory.creating_map(player);
                        if (map_idx2 != -1) {
                            if (plugin_memory.maps.get(plugin_memory.map_creators.get(map_idx2).map_idx).checkpoints.size() == 0) {
                                plugin_memory.maps.remove(plugin_memory.map_creators.get(map_idx2).map_idx);
                                plugin_memory.map_creators.remove(map_idx2);
                                player.sendMessage("§cNo map created! No checkpoints were marked!§r");
                                return true;
                            }
                            if (plugin_memory.maps.get(map_idx2).start == null) {
                                player.sendMessage("§eNo start location placed, psst use that mighty stone hoe of yours on a just as dirty block!§r");
                                return true;
                            }
                            plugin_memory.map_creators.remove(map_idx2);
                            plugin_memory.save_maps();
                            player.sendMessage("§aMap created!§r");
                        }
                        else {
                            player.sendMessage("§cNot creating map...§r");
                        }
                    }
                    else if (strings[0].equals("list")) {
                        player.sendMessage("Available maps:");
                        for (final map _map : plugin_memory.maps) {
                            player.sendMessage("§6" + _map.map_name + "§r");
                        }
                        player.sendMessage("Multimaps maps:");
                        for (final multimap _mmmap : plugin_memory.multimaps) {
                            player.sendMessage("§6" + _mmmap.multimap_name + "§r");
                        }
                    }
                    else if (strings[0].equals("load")) {
                        plugin_memory.load_maps();
                    }
                    else if (strings[0].equals("stop")) {
                        final int player_idx = plugin_memory.player_playing(player);
                        if (player_idx != -1) {
                            plugin_memory.current_playing_players.get(player_idx).restart = false;
                            elytra_movement.player_end(player_idx, "§eStopped by user§r");
                        }
                    }
                    else if (strings[0].equals("start") || strings[0].equals("highscore") || strings[0].equals("delete") || strings[0].equals("map")) {
                        player.sendMessage("§eYou have to give a map name!§r");
                    }
                    else {
                        player.sendMessage("§cUnknown command!§r");
                    }
                }
                else {
                    player.sendMessage("§cUknown amount of arguments: §r" + strings.length);
                }
            }
            return true;
        }
        if (commandSender instanceof Player && !((Player)commandSender).isOp()) {
            ((Player)commandSender).sendMessage("§cLul, must be OP to set this option...§r");
            return false;
        }
        if (strings.length != 2) {
            if (commandSender instanceof Player) {
                ((Player)commandSender).sendMessage("Unknown amount of arguments, should be '/elytra map_only_op true/false'");
            }
            else {
                System.out.println("Unknown amount of arguments, should be '/elytra map_only_op true/false'");
            }
        }
        else {
            plugin_memory.op_only = Boolean.parseBoolean(strings[1]);
            if (commandSender instanceof Player) {
                ((Player)commandSender).sendMessage("Set map_op_only to " + (plugin_memory.op_only ? "true" : "false"));
            }
            else {
                System.out.println("Set map_op_only to " + (plugin_memory.op_only ? "true" : "false"));
            }
            plugin_memory.save_maps();
        }
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender commandSender, final Command command, final String s, final String[] strings) {
        final List<String> ret = new ArrayList<String>();
        if (strings.length == 1) {
            ret.add("start");
            ret.add("loop");
            ret.add("multimap");
            ret.add("multistart");
            ret.add("stop");
            ret.add("map");
            ret.add("end");
            ret.add("delete");
            ret.add("list");
            ret.add("load");
            ret.add("highscore");
        }
        else if (strings[0].equals("start") || strings[0].equals("loop")) {
            for (final map _map : plugin_memory.maps) {
                ret.add(_map.map_name);
            }
        }
        else if (strings[0].equals("delete") || strings[0].equals("highscore")) {
            for (final map _map : plugin_memory.maps) {
                ret.add(_map.map_name);
            }
            for (final multimap _mmmap : plugin_memory.multimaps) {
                ret.add(_mmmap.multimap_name);
            }
        }
        else if (strings[0].equals("multistart")) {
            for (final multimap _mmmap : plugin_memory.multimaps) {
                ret.add(_mmmap.multimap_name);
            }
        }
        return ret;
    }
}
