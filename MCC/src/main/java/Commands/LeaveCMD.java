package Commands;

import Arena.ArenaList;
import Arena.Arena;
import Utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Leave implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(commandSender instanceof Player)) return true;

        Player player = (Player) commandSender;

        Arena arena = ArenaList.get(player);
        if (arena == null) {
            ChatUtil.sendMessage(player, "&cВы не подключены к арене!");
            return true;
        }

        arena.leave(player);
        ChatUtil.sendMessage(player, "&cВы отключились от арены!");
        return true;
    }
}
