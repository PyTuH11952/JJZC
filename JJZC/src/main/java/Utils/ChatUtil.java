package Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtil {

    private static final String preifx = "&a[&c&lJustJoy&a]: ";

    public static void sendMessage(Player player, String msg){

        player.sendMessage(ChatColor.translateAlternateColorCodes('&',preifx + msg));

    }
}
