package Events;

import Arena.ArenaList;
import Arena.Arena;
import Arena.ArtifactsTypes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        Arena arena = ArenaList.get(e.getEntity());
        if(arena.getGame().lifesCount > 0){
            if(arena.getPlayers().get(e.getEntity()).containsKey(ArtifactsTypes.CONTRACT)){
                if((int)(Math.random() * 10) <= arena.getPlayers().get(e.getEntity()).get(ArtifactsTypes.CONTRACT)){
                    return;
                }
            }
            arena.getGame().lifesCount -= 1;
        }else{
            arena.playerDie(e.getEntity());
        }
    }
}
