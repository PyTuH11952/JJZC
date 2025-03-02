package Events;

import Arena.Arena;
import Arena.ArenaList;
import Utils.ChatUtil;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;

import static com.mimikcraft.mcc.ExecutableApi.giveExecutableItem;

public class KillsEventListener implements Listener {

    @EventHandler
    public void onEntityDeath(MythicMobDeathEvent e){
        if(e.getMob().hasFaction()){
            Arena arena = ArenaList.get(e.getEntity().getWorld().getName());
            if(e.getMob().getFaction().equals("Zombie")){
                arena.getGame().mobs.remove(e.getEntity());
                arena.getGame().aliveZombies -= 1;
                if (e.getKiller() != null){
                    if (e.getKiller() instanceof Player){
                        Player player = ((Player) e.getKiller()).getPlayer();
                        if (arena.getGame().getPlayerKills().get(player) != null){
                            int kills = (arena.getGame().getPlayerKills().get(player)+1);
                            arena.getGame().getPlayerKills().put(player, kills);
                        } else{
                            arena.getGame().getPlayerKills().put(player, 1);
                        }
                    }
                }
                if (arena.getGame().aliveZombies == (arena.getLocation().getAddZombie()*2) && !arena.getGame().isGlowingTimer()){
                    arena.getGame().glowing();
                }
                if(arena.getGame().aliveZombies <= arena.getLocation().getAddZombie()){
                    arena.getGame().setBossbarProgress(0);
                    arena.getGame().startNewWave();
                }
                arena.getGame().sendBossBar();
            }

            if(e.getMob().getFaction().equals("Kaka")){
                arena.getGame().mobs.remove(e.getEntity());
                arena.getGame().aliveZombies -= 1;
                arena.getGame().sendBossBar();
                if(arena.getGame().aliveZombies <= arena.getGame().getAddZombie()){
                    for (Player player : arena.getPlayers().keySet()){
                        giveExecutableItem(player, "dopitem1", 1);
                        giveExecutableItem(player, "dopitem2", 3);
                        arena.getGame().setAddZombie(arena.getLocation().getAddZombie());
                    }
                    arena.getGame().startNewWave();
                }
            }

            if(e.getMob().getFaction().equals("BombZombie")){
                arena.getGame().mobs.remove(e.getEntity());
                arena.getGame().aliveZombies -= 1;
                arena.getGame().sendBossBar();
                if(arena.getGame().aliveZombies <= arena.getGame().getAddZombie()){
                    arena.getGame().setAddZombie(arena.getLocation().getAddZombie());
                    arena.getGame().startNewWave();
                }
            }

            if(e.getMob().getFaction().equals("MiniBoss")){
                arena.getGame().startNewWave();
                Location location = new Location(Bukkit.getWorld(e.getMob().getLocation().getWorld().getName()), e.getMob().getLocation().getX(), e.getMob().getLocation().getY(),e.getMob().getLocation().getZ());
                arena.getGame().spawnRandomArtifact(location);
            }

            if(e.getMob().getFaction().equals("Peniata")){
                arena.getGame().startNewWave();
            }

            if(e.getMob().getFaction().equals("Boss")){
                if (arena.getGame().isInfinity()){
                    arena.getGame().startNewWave();
                } else{
                    arena.getGame().win();
                }

            }
        }
    }
}
