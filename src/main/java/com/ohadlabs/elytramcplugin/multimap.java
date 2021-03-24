// 
// Decompiled by Procyon v0.5.36
// 

package com.ohadlabs.elytramcplugin;

import java.util.ArrayList;

class multimap
{
    String multimap_name;
    ArrayList<Integer> map_idxs;
    public ArrayList<com.ohadlabs.elytramcplugin.highscores> highscore;
    
    multimap(final String _map_name) {
        this.map_idxs = new ArrayList<Integer>();
        this.highscore = new ArrayList<com.ohadlabs.elytramcplugin.highscores>();
        this.multimap_name = _map_name;
    }
}
