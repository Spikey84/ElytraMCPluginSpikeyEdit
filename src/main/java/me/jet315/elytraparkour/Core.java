package me.jet315.elytraparkour;

import me.jet315.elytraparkour.commands.Boost;
import me.jet315.elytraparkour.commands.CommandHandler;
import me.jet315.elytraparkour.commands.Particles;
import me.jet315.elytraparkour.listeners.GlideMoveEvent;
import me.jet315.elytraparkour.listeners.GlideToggleEvent;
import me.jet315.elytraparkour.listeners.JoinEvent;
import me.jet315.elytraparkour.listeners.QuitEvent;
import me.jet315.elytraparkour.manager.ElytraManager;
import me.jet315.elytraparkour.manager.ElytraPlayer;
import me.jet315.elytraparkour.manager.ParticleManager;
import me.jet315.elytraparkour.utils.ParticleUtils;
import me.jet315.elytraparkour.utils.Properties;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.util.HashMap;

public class Core extends JavaPlugin {

    /**
     * Stores plugins instance
     */
    private static Core instance;

    public static Boolean particles = false;
    public static Boolean boost = false;
    public static Scoreboard board;
    public static int counterSeconds;
    public static int counterId;
    public static HashMap<Player, Integer> startTime = new HashMap<Player, Integer>();

    /**
     * Stores the configuration data
     */
    private Properties properties;

    /**
     * Stores active maps and players
     */
    private ElytraManager elytraManager;

    /**
     * Stores particle manager
     */
    private ParticleManager particleManager;

    public void onEnable(){
        board = Bukkit.getScoreboardManager().getMainScoreboard();
        instance = this;
        properties = new Properties(this);
        elytraManager = new ElytraManager(this);
        particleManager = new ParticleManager(this);

        getCommand("elytraparkour").setExecutor(new CommandHandler());
        getCommand("particles").setExecutor(new Particles());
        getCommand("boost").setExecutor(new Boost());

        Bukkit.getPluginManager().registerEvents(new GlideMoveEvent(),this);
        Bukkit.getPluginManager().registerEvents(new GlideToggleEvent(),this);
        Bukkit.getPluginManager().registerEvents(new JoinEvent(),this);
        Bukkit.getPluginManager().registerEvents(new QuitEvent(),this);

        for(Player p : Bukkit.getOnlinePlayers()){
            Core.getInstance().getElytraManager().getElytraPlayers().put(p,new ElytraPlayer(p));
        }

        counterId = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
            public void run() {

                counterSeconds++;

            }
        }, 0,20);
    }


    public void onDisable(){
        Bukkit.getScheduler().cancelTasks(this);
        particleManager = null;
        elytraManager = null;
        properties = null;
        instance = null;
    }

    public void reloadPlugin(){
        this.particleManager = null;
        this.properties = null;

        this.properties = new Properties(this);
        this.elytraManager = new ElytraManager(this);
    }

    /**
     * @return configuration properties
     */
    public Properties getProperties() {
        return properties;
    }

    public ElytraManager getElytraManager() {
        return elytraManager;
    }

    public static Core getInstance() {
        return instance;
    }


}

