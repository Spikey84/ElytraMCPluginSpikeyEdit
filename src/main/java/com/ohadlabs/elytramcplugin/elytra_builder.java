// 
// Decompiled by Procyon v0.5.36
// 

package com.ohadlabs.elytramcplugin;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.EventHandler;
import java.util.Collection;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.Listener;

public class elytra_builder implements Listener
{
    @EventHandler
    public void onPlayerBreakBlock(final BlockBreakEvent event) {
        if (plugin_memory.player_playing(event.getPlayer()) != -1) {
            event.setCancelled(true);
        }
        if (event.getPlayer().getItemInHand().getType() == Material.STONE_HOE) {
            int map_creation_idx;
            try {
                map_creation_idx = plugin_memory.map_creators.get(plugin_memory.creating_map(event.getPlayer())).map_idx;
            }
            catch (Exception e) {
                return;
            }
            if (map_creation_idx == -1) {
                return;
            }
            if (event.getBlock().getType() == Material.RED_WOOL || event.getBlock().getType() == Material.BLUE_WOOL || event.getBlock().getType() == Material.WHITE_WOOL) {
                if (map_creation_idx != -1) {
                    this.break_all_surroundig_blocks(event.getBlock(), map_creation_idx);
                    plugin_memory.maps.get(map_creation_idx).checkpoints.add(new ArrayList<loc_points>(plugin_memory.maps.get(map_creation_idx).new_checkpoint));
                    plugin_memory.maps.get(map_creation_idx).new_checkpoint.clear();
                    event.getPlayer().sendMessage("Current checkpoints: §b" + plugin_memory.maps.get(map_creation_idx).checkpoints.size() + "§r");
                }
            }
            else if (event.getBlock().getType() == Material.DIRT) {
                (plugin_memory.maps.get(map_creation_idx).start = event.getBlock().getLocation()).setY(plugin_memory.maps.get(map_creation_idx).start.getY() + 1.0);
                event.getBlock().setType(Material.AIR);
                event.getPlayer().sendMessage("Start location placed!");
            }
        }
    }
    
    @EventHandler
    public void onPlayerPlaceBlock(final BlockPlaceEvent event) {
        if (plugin_memory.player_playing(event.getPlayer()) != -1) {
            event.setCancelled(true);
        }
    }
    
    public void break_all_surroundig_blocks(final Block block, final int map_idx) {
        switch (block.getData()) {
            case 0: {
                plugin_memory.maps.get(map_idx).new_checkpoint.add(new loc_points(block.getLocation(), plugin_memory.location_types.BLOCK_NORMAL));
                break;
            }
            case 11: {
                plugin_memory.maps.get(map_idx).new_checkpoint.add(new loc_points(block.getLocation(), plugin_memory.location_types.BLOCK_SLOW));
                break;
            }
            case 14: {
                plugin_memory.maps.get(map_idx).new_checkpoint.add(new loc_points(block.getLocation(), plugin_memory.location_types.BLOCK_BOOST));
                break;
            }
            default: {
                return;
            }
        }
        block.setType(Material.AIR);
        if (block.getRelative(1, 0, 0).getType() == Material.RED_WOOL || block.getRelative(1, 0, 0).getType() == Material.BLUE_WOOL || block.getRelative(1, 0, 0).getType() == Material.WHITE_WOOL) {
            this.break_all_surroundig_blocks(block.getRelative(1, 0, 0), map_idx);
        }
        if (block.getRelative(-1, 0, 0).getType() == Material.RED_WOOL || block.getRelative(-1, 0, 0).getType() == Material.BLUE_WOOL || block.getRelative(-1, 0, 0).getType() == Material.WHITE_WOOL) {
            this.break_all_surroundig_blocks(block.getRelative(-1, 0, 0), map_idx);
        }
        if (block.getRelative(0, 1, 0).getType() == Material.RED_WOOL || block.getRelative(0, 1, 0).getType() == Material.BLUE_WOOL || block.getRelative(0, 1, 0).getType() == Material.WHITE_WOOL) {
            this.break_all_surroundig_blocks(block.getRelative(0, 1, 0), map_idx);
        }
        if (block.getRelative(0, -1, 0).getType() == Material.RED_WOOL || block.getRelative(0, -1, 0).getType() == Material.BLUE_WOOL || block.getRelative(0, -1, 0).getType() == Material.WHITE_WOOL) {
            this.break_all_surroundig_blocks(block.getRelative(0, -1, 0), map_idx);
        }
        if (block.getRelative(0, 0, 1).getType() == Material.RED_WOOL || block.getRelative(0, 0, 1).getType() == Material.BLUE_WOOL || block.getRelative(0, 0, 1).getType() == Material.WHITE_WOOL) {
            this.break_all_surroundig_blocks(block.getRelative(0, 0, 1), map_idx);
        }
        if (block.getRelative(0, 0, -1).getType() == Material.RED_WOOL || block.getRelative(0, 0, -1).getType() == Material.BLUE_WOOL || block.getRelative(0, 0, -1).getType() == Material.WHITE_WOOL) {
            this.break_all_surroundig_blocks(block.getRelative(0, 0, -1), map_idx);
        }
    }
}
