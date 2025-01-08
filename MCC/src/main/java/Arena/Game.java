package Arena;

import Utils.ChatUtil;
import com.mimikcraft.mcc.ExecutableApi;
import com.mimikcraft.mcc.Main;
import com.ssomar.score.usedapi.VaultAPI;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final Arena arena;

    private int hardlevel = 1;

    private int wave = 1;

    private int infinitywave = 1;

    private int zombiecount = 0;

    private int life = 3;

    private final List<Entity> mobs = new ArrayList<Entity>();

    public Game(Arena arena) {
        this.arena = arena;
    }

    public void start(){
        preparePlayers();
    }

    private void getkit(Player player){

        if (player.hasPermission("default")) {

            ExecutableApi.giveExecutableItem(player, "case1", 3);

        } else if (player.hasPermission("vip")) {
            ExecutableApi.giveExecutableItem(player, "case1", 10);
            ExecutableApi.giveExecutableItem(player, "bomba1", 3);
            ExecutableApi.giveExecutableItem(player, "bomba2", 2);
        } else if (player.hasPermission("vip+")) {
            ExecutableApi.giveExecutableItem(player, "case1", 10);
            ExecutableApi.giveExecutableItem(player, "case2", 3);
            ExecutableApi.giveExecutableItem(player, "bomba1", 5);
            ExecutableApi.giveExecutableItem(player, "bomba2", 3);

        } else if (player.hasPermission("premium")) {
            ExecutableApi.giveExecutableItem(player, "case1", 15);
            ExecutableApi.giveExecutableItem(player, "case2", 5);
            ExecutableApi.giveExecutableItem(player, "case3", 3);
            ExecutableApi.giveExecutableItem(player, "bomba1", 7);
            ExecutableApi.giveExecutableItem(player, "bomba2", 5);
            ExecutableApi.giveExecutableItem(player, "bomba3", 3);

        } else if (player.hasPermission("sponsor")) {
            ExecutableApi.giveExecutableItem(player, "case1", 16);
            ExecutableApi.giveExecutableItem(player, "case2", 6);
            ExecutableApi.giveExecutableItem(player, "case3", 4);
            ExecutableApi.giveExecutableItem(player, "bomba1", 8);
            ExecutableApi.giveExecutableItem(player, "bomba2", 6);
            ExecutableApi.giveExecutableItem(player, "bomba3", 4);

        } else if (player.hasPermission("elite")) {
            ExecutableApi.giveExecutableItem(player, "case1", 16);
            ExecutableApi.giveExecutableItem(player, "case2", 5);
            ExecutableApi.giveExecutableItem(player, "case3", 3);
            ExecutableApi.giveExecutableItem(player, "case4", 2);
            ExecutableApi.giveExecutableItem(player, "bomba1", 8);
            ExecutableApi.giveExecutableItem(player, "bomba2", 6);
            ExecutableApi.giveExecutableItem(player, "bomba3", 4);
            ExecutableApi.giveExecutableItem(player, "lom", 1);

        } else if (player.hasPermission("god")) {
            ExecutableApi.giveExecutableItem(player, "case1", 16);
            ExecutableApi.giveExecutableItem(player, "case2", 5);
            ExecutableApi.giveExecutableItem(player, "case3", 3);
            ExecutableApi.giveExecutableItem(player, "case4", 2);
            ExecutableApi.giveExecutableItem(player, "bomba1", 8);
            ExecutableApi.giveExecutableItem(player, "bomba2", 6);
            ExecutableApi.giveExecutableItem(player, "bomba3", 4);
            ExecutableApi.giveExecutableItem(player, "bomba5", 2);
            ExecutableApi.giveExecutableItem(player, "lom2", 1);

        }


    }
    private void preparePlayers(){
        for (Player player : arena.getPlayers()) {
            player.getInventory().clear();
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.teleport(new Location(Bukkit.getWorld("world"),1,2,1));
            ChatUtil.sendMessage(player,"&cБейся!");
            spawnmob(player.getLocation(), "SkeletonKing");
            spawnmob(player.getLocation(), "SkeletonKing");
            glowing();
            spawnmob(player.getLocation(), "SkeletonKing");
            spawnmob(player.getLocation(), "SkeletonKing");
            player.setGameMode(GameMode.ADVENTURE);
//            getkit(player);

        }
    }
    private void spawnmob(Location location, String name){
        new BukkitRunnable(){

            @Override

            public void run(){
                Location particle = location;
                particle.setX(particle.getX() - 0.5);
                particle.setZ(particle.getZ() - 0.5);

                Bukkit.getWorld("world").spawnParticle(Particle.FLAME, particle,10, 0.3, 0.3, 0.3, 0);
                Bukkit.getWorld("world").playSound(particle, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);

                cancel();
            }

        }.runTaskTimer(Main.getInstance(), 0L, 10L);

        ActiveMob mythicentity = MythicBukkit.inst().getMobManager().spawnMob(name, location, hardlevel);
        Entity entity = mythicentity.getEntity().getBukkitEntity();
        mobs.add(entity);
    }
    public void glowing(){
        for (Entity entity : mobs) {
            entity.setGlowing(true);
        }
    }
}
