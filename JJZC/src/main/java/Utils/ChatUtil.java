package Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtil {

    private static final String prefix = "&0[&6&lJustJoy&0]&7: ";

    public static void sendMessage(Player player, String msg){
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',prefix + msg));
    }
}
