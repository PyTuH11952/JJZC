package Commands;

import Arena.Arena;
import Arena.ArenaList;
import Utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddLifeCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player))
            return true;
        Player player = (Player) commandSender;

        if(ArenaList.hasArena(player)){
            Arena arena = ArenaList.get(player);
            arena.getGame().setLifesCount(arena.getGame().getLifesCount() + 1);
            return true;
        }else{
            ChatUtil.sendMessage(player, "&CВы находитесь не на арене!");
            return true;
        }
    }
}
