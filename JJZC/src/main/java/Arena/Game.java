package Arena;

import Utils.ChatUtil;
import com.mimikcraft.mcc.ExecutableApi;
import com.mimikcraft.mcc.Main;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import com.ssomar.score.usedapi.VaultAPI;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mimikcraft.mcc.ExecutableApi.giveExecutableItem;

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
            player.teleport(new Location(Bukkit.getWorld("world"),1,2,1));
            ChatUtil.sendMessage(player,"&cБейся!");
            player.setGameMode(GameMode.ADVENTURE);

        }
    }
    private void spawnMob(Location location, String name){
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
