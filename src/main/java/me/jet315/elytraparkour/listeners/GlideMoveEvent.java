package me.jet315.elytraparkour.listeners;

import me.jet315.elytraparkour.Core;
import me.jet315.elytraparkour.events.RingEnterEvent;
import me.jet315.elytraparkour.manager.ElytraMap;
import me.jet315.elytraparkour.manager.ElytraPlayer;
import me.jet315.elytraparkour.manager.Ring;
import me.jet315.elytraparkour.utils.RingType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class GlideMoveEvent implements Listener {

    private ArrayList<Player> dontCheckPlayer = new ArrayList<Player>();

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        //Player must be gliding through the rings
        if(Core.getInstance().getElytraManager().getElytraPlayers().get(e.getPlayer()).isInMap() && (e.getPlayer().getLocation().getBlock().getType().equals(Material.WATER) || e.getPlayer().getLocation().getBlock().getType().equals(Material.LAVA))) {
            Player p = e.getPlayer();
            p.teleport(Core.getInstance().getElytraManager().getElytraPlayers().get(p).getMap().getSpawnLocation());
            Core.getInstance().getElytraManager().getElytraPlayers().get(p).setInMap(false);
        }
        if (e.getPlayer().isGliding()) {

            if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) { // full block moved
                Player p = e.getPlayer();
                if(dontCheckPlayer.contains(p)) return;
                ElytraPlayer elytraPlayer = Core.getInstance().getElytraManager().getElytraPlayers().get(p);
                if (elytraPlayer != null) {

                    if (elytraPlayer.isInMap()) {
                        //check to see if at their portal
                        ElytraMap map = elytraPlayer.getMap();
                        if (map.getSpawnLocation().getWorld() == p.getLocation().getWorld()) {
                            Ring ring = map.getMapRings().get(elytraPlayer.getRingNumber() + 1);
                            //within the ring
                            if (ring.getCenterOfRing().distance(p.getLocation()) <= ring.getRadius()) {

                                //Create, and trigger the Ring Event
                                RingEnterEvent ringEnterEvent = new RingEnterEvent(p, ring);
                                Core.getInstance().getServer().getPluginManager().callEvent(ringEnterEvent);

                                if (ring.getRingType() == RingType.LAST) {
                                    p.getVelocity().multiply(Core.getInstance().getProperties().getLastRingBoost());
                                    p.playSound(p.getLocation(), Sound.ENTITY_VINDICATOR_CELEBRATE, 300, 100);
                                    p.spawnParticle(Core.getInstance().getProperties().getLastRingFeetParticles(), p.getLocation(), 30, 0, 0, 0, 0);
                                    if (Core.getInstance().getProperties().isTeleportToMapSpawnAtLastRing()) {
                                        p.setFallDistance(-999);
                                        p.setVelocity(new Vector(0, 0, 0));
                                        int finalScore = Core.counterSeconds - Core.startTime.get(p);
                                        if (Core.board.getObjective(map.getId()).getScore(p.getName()).getScore() > finalScore || Core.board.getObjective(map.getId()).getScore(p.getName()).getScore() == 0) {
                                            Core.board.getObjective(map.getId()).getScore(p.getName()).setScore(finalScore);
                                        }
                                        p.teleport(map.getSpawnLocation());
                                    } else {
                                        p.getWorld().spawnParticle(Particle.DRAGON_BREATH,p.getLocation(),100);
                                        if (Core.boost) {
                                            p.setVelocity(p.getLocation().getDirection().multiply(0.5 * Core.getInstance().getProperties().getLastRingBoost()));
                                        }
                                    }
                                    if (!Core.getInstance().getProperties().getMessageToSendWhenReachLastRing().equalsIgnoreCase("none")) {
                                        p.sendMessage(Core.getInstance().getProperties().getMessageToSendWhenReachLastRing().replaceAll("%PREFIX%", Core.getInstance().getProperties().getPluginsPrefix()).replaceAll("%MAP%", map.getId()));
                                    }

                                    elytraPlayer.setRingNumber(-1);
                                    elytraPlayer.setInMap(false);
                                    return;

                                } else if (ring.getRingType() == RingType.NORMAL) {
                                    if (Core.getInstance().getProperties().getDefaultRingBoost() > 0) {
                                        p.getWorld().spawnParticle(Particle.DRAGON_BREATH,p.getLocation(),100);
                                        if (Core.boost) {
                                            p.setVelocity(p.getLocation().getDirection().multiply(0.5 * Core.getInstance().getProperties().getDefaultRingBoost()));
                                        }
                                    }
                                    p.playSound(p.getLocation(), Core.getInstance().getProperties().getDefaultRingSound(), 100, 100);
                                    p.spawnParticle(Core.getInstance().getProperties().getDefaultRingFeetParticles(), p.getLocation(), 30, 0, 0, 0, 0);
                                    //add one to their score thing
                                    elytraPlayer.setRingNumber(elytraPlayer.getRingNumber() + 1);

                                    return;
                                }

                            }
                        }
                    }
                    //check if at starting ring
                    for (ElytraMap map : Core.getInstance().getElytraManager().getActiveMaps().values()) {
                        if (map.getSpawnLocation().getWorld() == p.getLocation().getWorld()) {
                            if (p.getLocation().distance(map.getStartingRing().getCenterOfRing()) <= map.getStartingRing().getRadius()) {

                                //Create, and trigger the Ring Event
                                RingEnterEvent ringEnterEvent = new RingEnterEvent(p, map.getStartingRing());
                                Core.getInstance().getServer().getPluginManager().callEvent(ringEnterEvent);


                                Core.startTime.put(p, Core.counterSeconds);
                                p.getWorld().spawnParticle(Particle.DRAGON_BREATH,p.getLocation(),100);
                                if (Core.boost) {
                                    p.setVelocity(p.getLocation().getDirection().multiply(Core.getInstance().getProperties().getFirstRingBoost()));
                                }
                                p.playSound(p.getLocation(), Core.getInstance().getProperties().getFirstRingSound(), 100, 100);
                                p.spawnParticle(Core.getInstance().getProperties().getFirstRingParticles(), p.getLocation(), 30, 0, 0, 0, 0);
                                elytraPlayer.setRingNumber(0);
                                elytraPlayer.setInMap(true);
                                elytraPlayer.setMap(map);
                                refreshPlayer(p, (int) map.getStartingRing().getRadius());

                            }
                        }
                    }
                }
            }

        }
    }

    public void refreshPlayer(Player p, int elytraRadius){
        dontCheckPlayer.add(p);
        final Player player = p;
        Bukkit.getServer().getScheduler().runTaskLater(Core.getInstance(), new Runnable() {
            public void run() {
                if(dontCheckPlayer.contains(player))
                dontCheckPlayer.remove(player);
            }
        },5 + elytraRadius);
    }
}
