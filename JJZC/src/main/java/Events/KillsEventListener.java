package Events;

import Arena.Arena;
import Arena.ArenaList;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.EventListener;

public class KillsEventListener implements Listener {

    @EventHandler
    public void onEntityDeath(MythicMobDeathEvent e){
        if(e.getMob().getFaction().equals("Zombie")){
            Arena arena = ArenaList.get(e.getEntity().getWorld().getName());
            arena.getGame().mobs.remove(e.getEntity());
            arena.getGame().aliveZomibes += 1;
            if(arena.getGame().aliveZomibes <= 2){
                arena.getGame().startNewWave();
            }
        }
    }
}
