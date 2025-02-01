package Arena;

import Utils.ChatUtil;
import com.mimikcraft.mcc.Main;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mimikcraft.mcc.ExecutableApi.giveExecutableItem;

public class Game {

    private final Arena arena;

    private int hardLevel = 1;

    private int wave = 1;

    private int wavesCount;

    private int infinityWave = 1;

    private int stage = 1;

    private int zombiesCount = 0;

    private int life = 3;

    public final List<Entity> mobs = new ArrayList<Entity>();
    public  int aliveZomibes = 0;

    public Game(Arena arena) {
        this.arena = arena;
    }

    public void start(){
        preparePlayers();
    }

    private void getkit(Player player){

        if (player.hasPermission("default")) {

            giveExecutableItem(player, "case1", 3);

        } else if (player.hasPermission("vip")) {
            giveExecutableItem(player, "case1", 10);
            giveExecutableItem(player, "bomba1", 3);
            giveExecutableItem(player, "bomba2", 2);
        } else if (player.hasPermission("vip+")) {
            giveExecutableItem(player, "case1", 10);
            giveExecutableItem(player, "case2", 3);
            giveExecutableItem(player, "bomba1", 5);
            giveExecutableItem(player, "bomba2", 3);

        } else if (player.hasPermission("premium")) {
            giveExecutableItem(player, "case1", 15);
            giveExecutableItem(player, "case2", 5);
            giveExecutableItem(player, "case3", 3);
            giveExecutableItem(player, "bomba1", 7);
            giveExecutableItem(player, "bomba2", 5);
            giveExecutableItem(player, "bomba3", 3);

        } else if (player.hasPermission("sponsor")) {
            giveExecutableItem(player, "case1", 16);
            giveExecutableItem(player, "case2", 6);
            giveExecutableItem(player, "case3", 4);
            giveExecutableItem(player, "bomba1", 8);
            giveExecutableItem(player, "bomba2", 6);
            giveExecutableItem(player, "bomba3", 4);

        } else if (player.hasPermission("elite")) {
            giveExecutableItem(player, "case1", 16);
            giveExecutableItem(player, "case2", 5);
            giveExecutableItem(player, "case3", 3);
            giveExecutableItem(player, "case4", 2);
            giveExecutableItem(player, "bomba1", 8);
            giveExecutableItem(player, "bomba2", 6);
            giveExecutableItem(player, "bomba3", 4);
            giveExecutableItem(player, "lom", 1);

        } else if (player.hasPermission("god")) {
            giveExecutableItem(player, "case1", 16);
            giveExecutableItem(player, "case2", 5);
            giveExecutableItem(player, "case3", 3);
            giveExecutableItem(player, "case4", 2);
            giveExecutableItem(player, "bomba1", 8);
            giveExecutableItem(player, "bomba2", 6);
            giveExecutableItem(player, "bomba3", 4);
            giveExecutableItem(player, "bomba5", 2);
            giveExecutableItem(player, "lom2", 1);

        }


    }
    private void preparePlayers(){
        for (Player player : arena.getPlayers()) {
            player.getInventory().clear();
            getkit(player);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            arena.setPlayerExp();
            player.teleport(arena.getLocation().getSpawnLocation());
            ChatUtil.sendMessage(player,"&cБейся!");
            player.setGameMode(GameMode.ADVENTURE);

        }
    }
    private void spawnMob(Location location, String name){
        new BukkitRunnable(){

            @Override
            public void run(){
                boolean isParticlesSpawned = false;
                if(!isParticlesSpawned){
                    arena.getArenaWorld().spawnParticle(Particle.FLAME, location,10, 0.3, 0.3, 0.3, 0);
                    arena.getArenaWorld().playSound(location, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);
                    isParticlesSpawned = true;
                }else{
                    ActiveMob mythicentity = MythicBukkit.inst().getMobManager().spawnMob(name, location, hardLevel);
                    Entity entity = mythicentity.getEntity().getBukkitEntity();
                    mobs.add(entity);
                    cancel();
                }
            }

        }.runTaskTimer(Main.getInstance(), 0L, 10L);

    }
    public void glowing(){
        for (Entity entity : mobs) {
            entity.setGlowing(true);
        }
    }

    public void startNewWave(){
        if(wavesCount == wave){{
            stage++;
            wavesCount = wavesCount + arena.getLocation().getStages().get(stage).wavesCount;
            wave++;
        }}
        zombiesCount = (int)((wavesCount+2)*arena.getPlayers().size()*arena.getLocation().getLocationFactor());
        arena.sendArenaTitle("Волна: " + wave, "Кол-во зомби: " + zombiesCount);
        new BukkitRunnable(){
            @Override
            public void run() {
                int spawnersCount = arena.getLocation().getStages().get(stage).spawners.size();
                int random = (int)(Math.random() * 10000);
                int temp = 0;
                String zombieName = "";
                for(Map.Entry<String, Double> entry : arena.getLocation().getZombies().entrySet()){
                    temp += (int) (entry.getValue() * 100);
                    if(random <= temp){
                        zombieName = entry.getKey();
                        break;
                    }
                }
                spawnMob(arena.getLocation().getStages().get(stage).spawners.get((int)(Math.random() * spawnersCount)), zombieName);
            }
        }.runTaskTimer(Main.getInstance(), 0L, 10L);
    }
}
