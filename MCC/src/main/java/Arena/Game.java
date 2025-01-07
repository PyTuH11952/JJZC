package Arena;

import Utils.ChatUtil;
import com.mimikcraft.mcc.ExecutableApi;
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

        ExecutableApi.giveExecutableItem(player, "Prem_Tornado_Blade", 1);

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
