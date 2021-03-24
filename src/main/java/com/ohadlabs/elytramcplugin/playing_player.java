// 
// Decompiled by Procyon v0.5.36
// 

package com.ohadlabs.elytramcplugin;

import org.bukkit.Location;
import org.bukkit.GameMode;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

class playing_player
{
    Player player;
    int map_idx;
    ItemStack[] inv;
    ItemStack[] armor;
    int current_checkpoint;
    long start_time;
    long join_time;
    boolean started;
    boolean restart;
    ArrayList<Integer> mm;
    int mm_idx;
    boolean mm_started;
    GameMode gm;
    Location start_pos;
    
    playing_player(final Player _player, final int _map_idx, final Location _start_pos) {
        this.mm = new ArrayList<Integer>();
        this.player = _player;
        this.map_idx = _map_idx;
        this.start_pos = _start_pos;
        this.current_checkpoint = 0;
    }
}
