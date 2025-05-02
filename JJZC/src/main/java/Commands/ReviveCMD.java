package Commands;

import Arena.Arena;
import Arena.ArenaList;
import Utils.ChatUtil;
import com.mimikcraft.mcc.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.Player;

public class ReviveCMD implements CommandExecutor {
    boolean чо = true;
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(чо){
            if(!(commandSender instanceof Player))
                return true;
            Player player = (Player) commandSender;

            if(ArenaList.hasArena(player)){
                Arena arena = ArenaList.get(player);
                arena.reviveRandom(player);
                return true;
            }else{
                ChatUtil.sendMessage(player, Messages.notOnArena);
                return true;
            }
        }else{
            System.out.println("А ничо");
        }

        return true;
    }
}
