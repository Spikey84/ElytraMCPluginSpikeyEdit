// 
// Decompiled by Procyon v0.5.36
// 

package com.ohadlabs.elytramcplugin;

import org.bukkit.Location;
import java.util.ArrayList;

class map
{
    public String map_name;
    public ArrayList<ArrayList<com.ohadlabs.elytramcplugin.loc_points>> checkpoints;
    public ArrayList<com.ohadlabs.elytramcplugin.loc_points> new_checkpoint;
    public Location start;
    public ArrayList<com.ohadlabs.elytramcplugin.highscores> highscore;
    
    map(final String _map_name) {
        this.checkpoints = new ArrayList<ArrayList<com.ohadlabs.elytramcplugin.loc_points>>();
        this.new_checkpoint = new ArrayList<com.ohadlabs.elytramcplugin.loc_points>();
        this.highscore = new ArrayList<com.ohadlabs.elytramcplugin.highscores>();
        this.map_name = _map_name;
    }
}
