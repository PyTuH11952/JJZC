package JustJoyDEV.Arena.Events;

import Arena.Arena;
import Arena.ArenaList;
import com.mimikcraft.mcc.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionEventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getWorld().getName().equals(Main.getInstance().getConfig().getString("baseWorldName"))) {
        } else {
            e.getPlayer().teleport(Bukkit.getWorld(Main.getInstance().getConfig().getString("baseWorldName")).getSpawnLocation());
        }
    }
    @EventHandler
    public void OnPlayerLeave(PlayerQuitEvent e){
        if (ArenaList.get(e.getPlayer()) != null){
            Arena arena = ArenaList.get(e.getPlayer());
            arena.leave(e.getPlayer());
        }
    }
}
