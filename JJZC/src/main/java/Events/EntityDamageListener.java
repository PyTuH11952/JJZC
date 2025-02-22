package Events;

import Arena.ArenaList;
import Arena.ArtifactsTypes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e){
        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) e;
            if(entityEvent.getEntity() instanceof Player){
                Player player = (Player) entityEvent.getEntity();
                if(ArenaList.get(player).getPlayers().get(player).containsKey(ArtifactsTypes.BONES)){
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give @e[distance=0..3,tag=zombie,type=zombie] instant_health");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give @e[distance=0..3,tag=zombie,type=!zombie] instant_damage");
                }
            }

        }
    }
}
