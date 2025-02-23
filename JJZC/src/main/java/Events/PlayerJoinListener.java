package Events;

import Arena.ArenaList;
import Arena.Arena;
import com.mimikcraft.mcc.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerChangedWorldEvent e){
        if(e.getFrom().getName().equals(Main.getInstance().getConfig().getString("baseWorldName"))){
            if(ArenaList.get(e.getPlayer()) != null){
                Arena arena = ArenaList.get(e.getPlayer());
                arena.join(e.getPlayer());
            }
        }
    }
}
