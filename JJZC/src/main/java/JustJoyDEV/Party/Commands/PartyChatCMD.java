package JustJoyDEV.Party.Commands;

import Party.PartyList;
import Utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PartyChatCMD implements CommandExecutor {
    private static final String prefix = "&0[&eПати&0]&7 ";
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player))
            return true;
        Player player = (Player) commandSender;
        if (PartyList.hasParty(player)) {
            if (args.length > 0) {
                for (UUID playerChatUuid : PartyList.getParty(player).getPartyPlayers()) {
                    Player playerChat = Bukkit.getPlayer(playerChatUuid);
                    playerChat.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "&f" + player.getDisplayName() + ": &7" + String.join(" ", args)));
                }
            } else {
                ChatUtil.sendMessage(player, "&cНельзя отправить пустое сообщение!");
            }
        } else {
            ChatUtil.sendMessage(player, "&cВы не состоите в пати!");
        }
        return true;
    }
}
