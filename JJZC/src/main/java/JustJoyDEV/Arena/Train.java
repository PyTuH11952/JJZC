package JustJoyDEV.Arena;

import com.mimikcraft.mcc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class Train {
    public List<Location> zone;
    public Location trainLocation;
    public Location newTrainLocation;
    public Location newSpawn;

    public Train(List<Location> zone, Location trainLocation, Location newTrainLocation, Location newSpawn) {
        this.zone = zone;
        this.trainLocation = trainLocation;
        this.newTrainLocation = newTrainLocation;
        this.newSpawn = newSpawn;
    }

    public boolean isPlayerOnTrain(Player player){
        Location location = player.getLocation();
        int playerX = (int)location.getX();
        int playerY = (int)location.getY();
        int playerZ = (int)location.getZ();
        for (Location trainLoc : zone){
            int locX = (int)trainLoc.getX();
            int locY = (int)trainLoc.getY();
            int locZ = (int)trainLoc.getZ();
            if (playerX == locX && playerY == locY && playerZ == locZ){
                return true;
            }
        }
        return  false;
    }

    public void startTrain(Arena arena){
        arena.getGame().clearMobs();
        for(UUID playerUuid : arena.getPlayers().keySet()){
            Player player = Bukkit.getPlayer(playerUuid);
            player.teleport(trainLocation);
        }
        new BukkitRunnable(){
            int ctr = 0;
            @Override
            public void run(){
                for (UUID playerUuid : arena.getPlayers().keySet()){
                    Player player = Bukkit.getPlayer(playerUuid);
                    player.getLocation().getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 100, 10, 10, 10, 0.1);
                    player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_ARMOR_STAND_BREAK, 1, 1);
                }
                ctr++;
                if(ctr >= 20){
                    for(UUID playerUuid : arena.getPlayers().keySet()){
                        Player player = Bukkit.getPlayer(playerUuid);
                        player.teleport(newTrainLocation);
                        arena.getGame().startNewWave();
                        arena.getGame().setTrain(null);
                        cancel();
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 10);
    }

}

