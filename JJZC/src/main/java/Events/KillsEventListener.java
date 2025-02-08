package Events;

import Arena.Arena;
import Arena.ArenaList;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class KillsEventListener implements Listener {

    @EventHandler
    public void onEntityDeath(MythicMobDeathEvent e){
        if(e.getMob().getFaction().equals("Zombie")){
            Arena arena = ArenaList.get(e.getEntity().getWorld().getName());
            arena.getGame().mobs.remove(e.getEntity());
            arena.getGame().aliveZombies += 1;
            if(arena.getGame().aliveZombies <= 2){
                arena.getGame().startNewWave();
            }
        }
    }
}
