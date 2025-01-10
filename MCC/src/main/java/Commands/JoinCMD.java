package Commands;

import Arena.Arena;
import Arena.ArenaList;
import Utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Join implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player)) return true;

        Player player = (Player) commandSender;

        if (args.length < 1) {
            ChatUtil.sendMessage(player,"&cИспользуйте: /join <арена>");
            return  true;
        }

        if (!ArenaList.hasArena(args[0])) {
            ChatUtil.sendMessage(player, "&cТакой арены нет!");
            return true;
        }
        Arena arena = ArenaList.get(args[0]);
        arena.join(player);

        return true;
    }
}