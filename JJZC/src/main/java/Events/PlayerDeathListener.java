package Events;

import Arena.ArenaList;
import Arena.Arena;
import Arena.Artifact;
import Arena.ArtifactsTypes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        Arena arena = ArenaList.get(e.getEntity());
        Player player = e.getEntity();
        if(arena.getGame().getLifesCount() > 0){
            if(ArenaList.get(player) == null){
                return;
            }
            for(Artifact artifact : ArenaList.get(player).getPlayers().get(player)){
                if(artifact.artifactType == ArtifactsTypes.CONTRACT){
                    if((int)(Math.random() * 10) <= artifact.level){
                        player.getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('&', "&aВас спасла сила контракта!"), "");
                        arena.sendArenaMessage("&aИгрок &e" + player.getPlayer() + " &aизбежал смерти благодаря &eконтракту");
                        return;
                    }
                }
            }
            arena.getGame().setLifesCount(arena.getGame().getLifesCount()-1);;
        }else{
            arena.playerDie(player);
        }
    }
}
