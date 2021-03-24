// 
// Decompiled by Procyon v0.5.36
// 

package com.ohadlabs.elytramcplugin;

import com.ohadlabs.elytramcplugin.loc_points;
import com.ohadlabs.elytramcplugin.plugin_memory;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import java.text.DecimalFormat;
import org.bukkit.Sound;
import java.util.ArrayList;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.Listener;

public class elytra_movement implements Listener
{
    static final String win_message = "§aYay you completed the map! GZ§r";
    static final String win_message_multi = "§aYay you completed the multimap! GZ§r";
    static final String lose_message = "§eOh you didn't complete... how sad :-(§r";
    
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final int player_playing_idx = plugin_memory.player_playing(event.getPlayer());
        if (player_playing_idx == -1) {
            return;
        }
        event.getPlayer().removePotionEffect(PotionEffectType.JUMP);
        event.getPlayer().removePotionEffect(PotionEffectType.SPEED);
        if (event.getPlayer().getFallDistance() != 0.0f && !plugin_memory.current_playing_players.get(player_playing_idx).started && System.currentTimeMillis() - plugin_memory.current_playing_players.get(player_playing_idx).join_time > 1500L) {
            plugin_memory.current_playing_players.get(player_playing_idx).started = true;
        }
        if (plugin_memory.maps.get(plugin_memory.current_playing_players.get(player_playing_idx).map_idx).checkpoints.size() <= plugin_memory.current_playing_players.get(player_playing_idx).current_checkpoint) {
            return;
        }
        final Location plr_loc = event.getPlayer().getLocation();
        if (plr_loc.distance(plugin_memory.maps.get(plugin_memory.current_playing_players.get(player_playing_idx).map_idx).checkpoints.get(plugin_memory.current_playing_players.get(player_playing_idx).current_checkpoint).get(0).loc) < 12.0) {
            for (final loc_points location : plugin_memory.maps.get(plugin_memory.current_playing_players.get(player_playing_idx).map_idx).checkpoints.get(plugin_memory.current_playing_players.get(player_playing_idx).current_checkpoint)) {
                if (plr_loc.distance(location.loc) < (Math.max(event.getPlayer().getVelocity().length(), 1.0))) {
                    if (plugin_memory.current_playing_players.get(player_playing_idx).current_checkpoint == 0 && !plugin_memory.current_playing_players.get(player_playing_idx).mm_started) {
                        plugin_memory.current_playing_players.get(player_playing_idx).start_time = System.currentTimeMillis();
                    }
                    if (location.loc_type == plugin_memory.location_types.BLOCK_BOOST) {
                        event.getPlayer().setVelocity(event.getPlayer().getVelocity().add(event.getPlayer().getLocation().getDirection().multiply(event.getPlayer().getVelocity().length())));
                    }
                    if (location.loc_type == plugin_memory.location_types.BLOCK_SLOW) {
                        event.getPlayer().setVelocity(event.getPlayer().getVelocity().multiply(0.5));
                    }
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1.0f, 1.0f);
                    event.getPlayer().sendMessage("Went through §a" + ++plugin_memory.current_playing_players.get(player_playing_idx).current_checkpoint + "§r / §a" + plugin_memory.maps.get(plugin_memory.current_playing_players.get(player_playing_idx).map_idx).checkpoints.size() + "§r checkpoints!");
                    if (plugin_memory.current_playing_players.get(player_playing_idx).current_checkpoint == plugin_memory.maps.get(plugin_memory.current_playing_players.get(player_playing_idx).map_idx).checkpoints.size()) {
                        if (plugin_memory.current_playing_players.get(player_playing_idx).mm.size() != 0) {
                            plugin_memory.current_playing_players.get(player_playing_idx).mm_started = true;
                            plugin_memory.current_playing_players.get(player_playing_idx).mm.remove(0);
                            if (plugin_memory.current_playing_players.get(player_playing_idx).mm.size() == 0) {
                                final DecimalFormat df = new DecimalFormat("0.00");
                                final long end_time = System.currentTimeMillis() - plugin_memory.current_playing_players.get(player_playing_idx).start_time;
                                plugin_memory.current_playing_players.get(player_playing_idx).restart = false;
                                plugin_memory.add_to_mm_highscore(plugin_memory.current_playing_players.get(player_playing_idx).mm_idx, end_time, event.getPlayer().getDisplayName());
                                Bukkit.broadcastMessage(event.getPlayer().getDisplayName() + " ended multimap: §b" + plugin_memory.multimaps.get(plugin_memory.current_playing_players.get(player_playing_idx).mm_idx).multimap_name + "§r in time: §a" + df.format(end_time / 1000.0) + "§r");
                                event.getPlayer().sendMessage("Highscores for this map:");
                                for (int i = 0; i < plugin_memory.multimaps.get(plugin_memory.current_playing_players.get(player_playing_idx).mm_idx).highscore.size(); ++i) {
                                    event.getPlayer().sendMessage(i + 1 + ". " + df.format(plugin_memory.multimaps.get(plugin_memory.current_playing_players.get(player_playing_idx).mm_idx).highscore.get(i).time / 1000.0) + " " + plugin_memory.multimaps.get(plugin_memory.current_playing_players.get(player_playing_idx).mm_idx).highscore.get(i).name);
                                }
                                player_end(player_playing_idx, "§aYay you completed the multimap! GZ§r");
                                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1.0f, 1.0f);
                            }
                            else {
                                plugin_memory.current_playing_players.get(player_playing_idx).map_idx = plugin_memory.current_playing_players.get(player_playing_idx).mm.get(0);
                                player_end(player_playing_idx, "§aYay you completed this level, only §b" + plugin_memory.current_playing_players.get(player_playing_idx).mm.size() + "§a levels back!§r");
                            }
                        }
                        else {
                            final DecimalFormat df = new DecimalFormat("0.00");
                            final long end_time = System.currentTimeMillis() - plugin_memory.current_playing_players.get(player_playing_idx).start_time;
                            final int map_idx = plugin_memory.current_playing_players.get(player_playing_idx).map_idx;
                            plugin_memory.add_to_highscore(plugin_memory.current_playing_players.get(player_playing_idx).map_idx, end_time, event.getPlayer().getDisplayName());
                            Bukkit.broadcastMessage(event.getPlayer().getDisplayName() + " ended map: §b" + plugin_memory.maps.get(map_idx).map_name + "§r in time: §a" + df.format(end_time / 1000.0) + "§r");
                            player_end(player_playing_idx, "§aYay you completed the map! GZ§r");
                            event.getPlayer().sendMessage("Highscores for this map:");
                            for (int j = 0; j < plugin_memory.maps.get(map_idx).highscore.size(); ++j) {
                                event.getPlayer().sendMessage(j + 1 + ". " + df.format(plugin_memory.maps.get(map_idx).highscore.get(j).time / 1000.0) + " " + plugin_memory.maps.get(map_idx).highscore.get(j).name);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerDisconnect(final PlayerQuitEvent event) {
        final int player_idx = plugin_memory.player_playing(event.getPlayer());
        if (player_idx != -1) {
            plugin_memory.current_playing_players.get(player_idx).restart = false;
            player_end(player_idx, "Disconnect!");
        }
    }
    
    @EventHandler
    public void onPlayerDrop(final PlayerDropItemEvent event) {
        final int player_idx = plugin_memory.player_playing(event.getPlayer());
        if (player_idx != -1) {
            event.setCancelled(true);
        }
    }
    
    public static void player_end(final int idx, final String message) {
        plugin_memory.current_playing_players.get(idx).player.setVelocity(plugin_memory.current_playing_players.get(idx).player.getVelocity().multiply(0));
        if (plugin_memory.current_playing_players.get(idx).restart) {
            plugin_memory.current_playing_players.get(idx).player.sendMessage(message);
            if (plugin_memory.current_playing_players.get(idx).mm.size() == 0) {
                plugin_memory.current_playing_players.get(idx).player.sendMessage("§bRESTARTING!§r Type §a\"/elytra stop\"§r to stop restarting.");
            }
            plugin_memory.current_playing_players.get(idx).current_checkpoint = 0;
            plugin_memory.current_playing_players.get(idx).started = false;
            plugin_memory.current_playing_players.get(idx).join_time = System.currentTimeMillis();
            plugin_memory.current_playing_players.get(idx).player.teleport(plugin_memory.maps.get(plugin_memory.current_playing_players.get(idx).map_idx).start);
        }
        else {
            final Location to_port_to = plugin_memory.current_playing_players.get(idx).start_pos;
            to_port_to.setY(to_port_to.getY() + 0.1);
            plugin_memory.current_playing_players.get(idx).player.setFireTicks(0);
            plugin_memory.current_playing_players.get(idx).player.teleport(to_port_to);
            plugin_memory.current_playing_players.get(idx).player.setGameMode(plugin_memory.current_playing_players.get(idx).gm);
            if (plugin_memory.current_playing_players.get(idx).gm == GameMode.CREATIVE) {
                plugin_memory.current_playing_players.get(idx).player.setAllowFlight(true);
            }
            plugin_memory.current_playing_players.get(idx).player.sendMessage(message);
            plugin_memory.current_playing_players.get(idx).player.getInventory().clear();
            for (int i = 0; i < plugin_memory.current_playing_players.get(idx).player.getInventory().getSize(); ++i) {
                try {
                    plugin_memory.current_playing_players.get(idx).player.getInventory().setItem(i, plugin_memory.current_playing_players.get(idx).inv[i]);
                }
                catch (Exception e) {
                    System.out.println("Inventory out of bounds");
                }
            }
            for (int i = 0; i < 4; ++i) {
                try {
                    plugin_memory.current_playing_players.get(idx).player.getInventory().setItem(36 + i, plugin_memory.current_playing_players.get(idx).armor[i]);
                }
                catch (Exception e) {
                    System.out.println("Armor out of bounds");
                }
            }
            plugin_memory.current_playing_players.get(idx).player.updateInventory();
            plugin_memory.current_playing_players.remove(idx);
        }
    }
}
