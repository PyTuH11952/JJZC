package Arena;

import Utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){

        Location limbo = new Location(Bukkit.getWorld("limbo"), 0, 25, 0);

        Player player = event.getPlayer();

        if (event.getPlayer().getWorld() != Bukkit.getWorld("world")){
            ChatUtil.sendMessage(event.getPlayer(), "&cОбнаружена незавершенная игра! Хотите продолжить?");
            player.teleport(limbo);
        }


    }

}
