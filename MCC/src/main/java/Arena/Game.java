package Arena;

import Utils.ChatUtil;
import com.mimikcraft.mcc.ExecutableApi;
import com.ssomar.score.usedapi.VaultAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class Game {

    private final Arena arena;

    private Round round;

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
            getkit(player);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.teleport(new Location(Bukkit.getWorld("world"),1,2,1));
            ChatUtil.sendMessage(player,"&cБейся!");
            arena.spawnmob(player.getLocation(), "SkeletonKing");
            player.setGameMode(GameMode.ADVENTURE);

        }
    }
}
