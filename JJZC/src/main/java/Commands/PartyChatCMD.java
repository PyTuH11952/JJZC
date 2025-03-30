package Commands;

import Arena.Arena;
import Arena.ArenaList;
import Party.PartyList;
import Utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyChatCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player))
            return true;
        Player player = (Player) commandSender;
        if (PartyList.hasParty(player)) {
            if (args.length > 0) {
                PartyList.getParty(player).sendPartyMessage(String.join(" ", args));
            } else {
                ChatUtil.sendMessage(player, "&cНельзя отправить пустое сообщение!");
            }
        } else {
            ChatUtil.sendMessage(player, "&cВы не состоите в пати!");
        }
        return true;
    }
}
