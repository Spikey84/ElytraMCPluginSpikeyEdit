// 
// Decompiled by Procyon v0.5.36
// 

package com.ohadlabs.elytramcplugin;

import org.bukkit.Location;
import org.bukkit.Bukkit;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.io.PrintWriter;
import org.bukkit.entity.Player;
import java.util.ArrayList;

public class plugin_memory
{
    public static ArrayList<com.ohadlabs.elytramcplugin.map> maps;
    public static ArrayList<com.ohadlabs.elytramcplugin.multimap> multimaps;
    public static ArrayList<com.ohadlabs.elytramcplugin.creating_map> map_creators;
    public static ArrayList<com.ohadlabs.elytramcplugin.playing_player> current_playing_players;
    public static boolean op_only;
    
    public static int creating_map(final Player player) {
        for (int i = 0; i < plugin_memory.map_creators.size(); ++i) {
            if (plugin_memory.map_creators.get(i).player == player) {
                return i;
            }
        }
        return -1;
    }
    
    public static int player_playing(final Player player) {
        for (int i = 0; i < plugin_memory.current_playing_players.size(); ++i) {
            if (plugin_memory.current_playing_players.get(i).player == player) {
                return i;
            }
        }
        return -1;
    }
    
    public static void save_maps() {
        try {
            final PrintWriter writer = new PrintWriter("maps.save", "UTF-8");
            writer.println("op:" + (plugin_memory.op_only ? "true" : "false"));
            for (final com.ohadlabs.elytramcplugin.map map_to_save : plugin_memory.maps) {
                writer.println("<map");
                writer.println("name: " + map_to_save.map_name);
                writer.println("start," + map_to_save.start.getWorld().getName() + "," + map_to_save.start.getBlockX() + "," + map_to_save.start.getBlockY() + "," + map_to_save.start.getBlockZ());
                writer.println("<highscore");
                for (final com.ohadlabs.elytramcplugin.highscores score : map_to_save.highscore) {
                    writer.println("score: " + score.time + " " + score.name);
                }
                writer.println(">highscore");
                writer.println("<checkpoints");
                for (final ArrayList<com.ohadlabs.elytramcplugin.loc_points> points : map_to_save.checkpoints) {
                    writer.println("<points");
                    for (final com.ohadlabs.elytramcplugin.loc_points point : points) {
                        String point_name = "point";
                        if (point.loc_type == location_types.BLOCK_BOOST) {
                            point_name += "b";
                        }
                        else if (point.loc_type == location_types.BLOCK_SLOW) {
                            point_name += "s";
                        }
                        writer.println(point_name + "," + point.loc.getWorld().getName() + "," + point.loc.getBlockX() + "," + point.loc.getBlockY() + "," + point.loc.getBlockZ());
                    }
                    writer.println(">points");
                }
                writer.println(">checkpoints");
                writer.println(">map");
            }
            for (final com.ohadlabs.elytramcplugin.multimap mmmap_to_save : plugin_memory.multimaps) {
                writer.println("<multimap");
                writer.println("mmname: " + mmmap_to_save.multimap_name);
                writer.print("mmmaps: ");
                for (final int mapidx : mmmap_to_save.map_idxs) {
                    writer.print(mapidx + " ");
                }
                writer.print("\n");
                writer.println("<mmhighscore");
                for (final com.ohadlabs.elytramcplugin.highscores score : mmmap_to_save.highscore) {
                    writer.println("mmscore: " + score.time + " " + score.name);
                }
                writer.println(">mmhighscore");
                writer.println(">multimap");
            }
            writer.close();
        }
        catch (Exception e) {
            System.out.print(e);
        }
    }
    
    public static void load_maps() {
        try {
            plugin_memory.maps.clear();
            final BufferedReader br = new BufferedReader(new FileReader("maps.save"));
            String line = br.readLine();
            final com.ohadlabs.elytramcplugin.map map_to_add = new com.ohadlabs.elytramcplugin.map("temp_name");
            final com.ohadlabs.elytramcplugin.multimap mm_map_to_add = new com.ohadlabs.elytramcplugin.multimap("mm_temp_name");
            while (line != null) {
                if (line.equals("op")) {
                    plugin_memory.op_only = Boolean.parseBoolean(line.split(":")[1]);
                }
                if (line.equals("<map")) {
                    map_to_add.checkpoints.clear();
                    map_to_add.new_checkpoint.clear();
                }
                if (line.startsWith("name:")) {
                    map_to_add.map_name = line.split(" ")[1];
                }
                if (line.startsWith("mmname:")) {
                    mm_map_to_add.multimap_name = line.split(" ")[1];
                }
                if (line.startsWith("mmmaps:")) {
                    final String[] map_indexes = line.split(" ");
                    for (int i = 1; i < map_indexes.length; ++i) {
                        mm_map_to_add.map_idxs.add(Integer.parseInt(map_indexes[i]));
                    }
                }
                if (line.startsWith("start,")) {
                    final String[] cords = line.split(",");
                    map_to_add.start = new Location(Bukkit.getWorld(cords[1]), Double.parseDouble(cords[2]), Double.parseDouble(cords[3]), Double.parseDouble(cords[4]));
                }
                if (line.equals("<checkpoints")) {
                    map_to_add.checkpoints.clear();
                }
                if (line.equals("<highscore")) {
                    map_to_add.highscore.clear();
                }
                if (line.startsWith("score:")) {
                    map_to_add.highscore.add(new com.ohadlabs.elytramcplugin.highscores(Long.parseLong(line.split(" ")[1]), line.split(" ")[2]));
                }
                if (line.equals("<mmhighscore")) {
                    mm_map_to_add.highscore.clear();
                }
                if (line.startsWith("mmscore:")) {
                    mm_map_to_add.highscore.add(new com.ohadlabs.elytramcplugin.highscores(Long.parseLong(line.split(" ")[1]), line.split(" ")[2]));
                }
                if (line.equals("<points")) {
                    map_to_add.checkpoints.add(new ArrayList<com.ohadlabs.elytramcplugin.loc_points>());
                }
                if (line.startsWith("point,")) {
                    final String[] cords = line.split(",");
                    map_to_add.checkpoints.get(map_to_add.checkpoints.size() - 1).add(new com.ohadlabs.elytramcplugin.loc_points(new Location(Bukkit.getWorld(cords[1]), Double.parseDouble(cords[2]), Double.parseDouble(cords[3]), Double.parseDouble(cords[4])), location_types.BLOCK_NORMAL));
                }
                if (line.startsWith("pointb,")) {
                    final String[] cords = line.split(",");
                    map_to_add.checkpoints.get(map_to_add.checkpoints.size() - 1).add(new com.ohadlabs.elytramcplugin.loc_points(new Location(Bukkit.getWorld(cords[1]), Double.parseDouble(cords[2]), Double.parseDouble(cords[3]), Double.parseDouble(cords[4])), location_types.BLOCK_BOOST));
                }
                if (line.startsWith("points,")) {
                    final String[] cords = line.split(",");
                    map_to_add.checkpoints.get(map_to_add.checkpoints.size() - 1).add(new com.ohadlabs.elytramcplugin.loc_points(new Location(Bukkit.getWorld(cords[1]), Double.parseDouble(cords[2]), Double.parseDouble(cords[3]), Double.parseDouble(cords[4])), location_types.BLOCK_SLOW));
                }
                if (line.equals(">map")) {
                    final com.ohadlabs.elytramcplugin.map _map = new com.ohadlabs.elytramcplugin.map(map_to_add.map_name);
                    _map.start = map_to_add.start;
                    for (final ArrayList<com.ohadlabs.elytramcplugin.loc_points> points : map_to_add.checkpoints) {
                        _map.checkpoints.add(points);
                    }
                    for (final com.ohadlabs.elytramcplugin.highscores score : map_to_add.highscore) {
                        _map.highscore.add(score);
                    }
                    plugin_memory.maps.add(_map);
                }
                if (line.equals(">multimap")) {
                    final com.ohadlabs.elytramcplugin.multimap _mmmap = new com.ohadlabs.elytramcplugin.multimap(mm_map_to_add.multimap_name);
                    for (final int mapidx : mm_map_to_add.map_idxs) {
                        _mmmap.map_idxs.add(mapidx);
                    }
                    for (final com.ohadlabs.elytramcplugin.highscores score : mm_map_to_add.highscore) {
                        _mmmap.highscore.add(score);
                    }
                    plugin_memory.multimaps.add(_mmmap);
                }
                line = br.readLine();
            }
            Bukkit.broadcastMessage("Maps loaded!");
        }
        catch (Exception e) {
            System.out.print(e);
        }
    }
    
    public static void add_to_highscore(final int map_idx, final long time, final String player_name) {
        final com.ohadlabs.elytramcplugin.highscores score = new com.ohadlabs.elytramcplugin.highscores();
        score.time = time;
        score.name = player_name;
        if (plugin_memory.maps.get(map_idx).highscore.size() == 0) {
            plugin_memory.maps.get(map_idx).highscore.add(score);
        }
        else {
            boolean added = false;
            for (int i = 0; i < plugin_memory.maps.get(map_idx).highscore.size(); ++i) {
                if (plugin_memory.maps.get(map_idx).highscore.get(i).time > time) {
                    plugin_memory.maps.get(map_idx).highscore.add(i, score);
                    added = true;
                    break;
                }
            }
            if (!added && plugin_memory.maps.get(map_idx).highscore.size() < 3) {
                plugin_memory.maps.get(map_idx).highscore.add(score);
            }
            if (plugin_memory.maps.get(map_idx).highscore.size() > 3) {
                plugin_memory.maps.get(map_idx).highscore.remove(3);
            }
        }
        save_maps();
    }
    
    public static void add_to_mm_highscore(final int mm_map_idx, final long time, final String player_name) {
        final com.ohadlabs.elytramcplugin.highscores score = new com.ohadlabs.elytramcplugin.highscores();
        score.time = time;
        score.name = player_name;
        if (plugin_memory.multimaps.get(mm_map_idx).highscore.size() == 0) {
            plugin_memory.multimaps.get(mm_map_idx).highscore.add(score);
        }
        else {
            boolean added = false;
            for (int i = 0; i < plugin_memory.multimaps.get(mm_map_idx).highscore.size(); ++i) {
                if (plugin_memory.multimaps.get(mm_map_idx).highscore.get(i).time > time) {
                    plugin_memory.multimaps.get(mm_map_idx).highscore.add(i, score);
                    added = true;
                    break;
                }
            }
            if (!added && plugin_memory.multimaps.get(mm_map_idx).highscore.size() < 3) {
                plugin_memory.multimaps.get(mm_map_idx).highscore.add(score);
            }
            if (plugin_memory.multimaps.get(mm_map_idx).highscore.size() > 3) {
                plugin_memory.multimaps.get(mm_map_idx).highscore.remove(3);
            }
        }
        save_maps();
    }
    
    static {
        plugin_memory.maps = new ArrayList<com.ohadlabs.elytramcplugin.map>();
        plugin_memory.multimaps = new ArrayList<com.ohadlabs.elytramcplugin.multimap>();
        plugin_memory.map_creators = new ArrayList<com.ohadlabs.elytramcplugin.creating_map>();
        plugin_memory.current_playing_players = new ArrayList<com.ohadlabs.elytramcplugin.playing_player>();
    }
    
    public enum location_types
    {
        BLOCK_NORMAL, 
        BLOCK_BOOST, 
        BLOCK_SLOW;
    }
}
