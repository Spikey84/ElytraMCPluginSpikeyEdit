// 
// Decompiled by Procyon v0.5.36
// 

package com.ohadlabs.elytramcplugin;

import org.bukkit.Location;

class loc_points
{
    Location loc;
    plugin_memory.location_types loc_type;
    
    loc_points(final Location _loc, final plugin_memory.location_types _loc_type) {
        this.loc = _loc;
        this.loc_type = _loc_type;
    }
    
    static plugin_memory.location_types get_loc_type(final String loc_type_to_get) {
        switch (loc_type_to_get) {
            case "S": {
                return plugin_memory.location_types.BLOCK_SLOW;
            }
            case "B": {
                return plugin_memory.location_types.BLOCK_BOOST;
            }
            default: {
                return plugin_memory.location_types.BLOCK_NORMAL;
            }
        }
    }
}
