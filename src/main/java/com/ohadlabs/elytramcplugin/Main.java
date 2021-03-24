// 
// Decompiled by Procyon v0.5.36
// 

package com.ohadlabs.elytramcplugin;

import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
    public void onEnable() {
        this.getCommand("elytra").setExecutor((CommandExecutor)new com.ohadlabs.elytramcplugin.elytra_cmd());
        this.getServer().getPluginManager().registerEvents((Listener)new com.ohadlabs.elytramcplugin.elytra_builder(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new com.ohadlabs.elytramcplugin.elytra_movement(), (Plugin)this);
        plugin_memory.load_maps();
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new Runnable() {
            @Override
            public void run() {
                if (plugin_memory.current_playing_players.size() != 0) {
                    for (int i = 0; i < plugin_memory.current_playing_players.size(); ++i) {
                        try {
                            if ((plugin_memory.current_playing_players.get(i).player.isOnGround() || plugin_memory.current_playing_players.get(i).player.getInventory().getChestplate().getType() != Material.ELYTRA) && plugin_memory.current_playing_players.get(i).started) {
                                com.ohadlabs.elytramcplugin.elytra_movement.player_end(i, "§eOh you didn't complete... how sad :-(§r");
                            }
                        }
                        catch (Exception ex) {}
                    }
                }
            }
        }, 1L, 1L);
    }
    
    public void onDisable() {
    }
}
