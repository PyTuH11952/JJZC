package JustJoyDEV.Arena.Events;

import Arena.Arena;
import Arena.ArenaList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveEventListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        if (ArenaList.get(player) == null){
            return;
        }
        Arena arena = ArenaList.get(player);
        if(arena.getGame().getTrain() == null){
            return;
        } else {
          if (arena.getGame().getTrain().isPlayerOnTrain(player)){
              arena.getGame().getTrain().startTrain(arena);
          }
        }
    }
}