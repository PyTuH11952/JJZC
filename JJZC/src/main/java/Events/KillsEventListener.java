package Events;

import Arena.Arena;
import Arena.ArenaList;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class KillsEventListener implements Listener {

    @EventHandler
    public void onEntityDeath(MythicMobDeathEvent e){
        if(e.getMob().hasFaction()){
            Arena arena = ArenaList.get(e.getEntity().getWorld().getName());
            if(e.getMob().getFaction().equals("Zombie")){
                arena.getGame().mobs.remove(e.getEntity());
                arena.getGame().aliveZombies -= 1;
                arena.getGame().sendBossBar();
                if(arena.getGame().aliveZombies <= arena.getLocation().getAddZombie()){
                    arena.getGame().startNewWave();
                }
            }
            if(e.getMob().getFaction().equals("Boss")){
                if (arena.getGame().isInfinity()){
                    arena.getGame().startNewWave();
                } else{
                    arena.getGame().endGame();
                }

            }
        }
    }
}
