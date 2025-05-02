package JustJoyDEV.Arena.Commands;

import Arena.Arena;
import Arena.ArenaList;
import Arena.ArenaStages;
import Utils.ChatUtil;
import com.mimikcraft.mcc.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ForceStartCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)){
            return true;
        }
        Player player = (Player) commandSender;
        List<Arena> availableArenas = ArenaList.getAvailable(player);
        for(Arena arena: availableArenas){
            if(arena.getArenaStage() == ArenaStages.STARTING && arena.getArenaStage() == ArenaStages.CUTSCENE){
                arena.join(player);
                return true;
            }
        }

        for(Arena arena: availableArenas){
            if(arena.getArenaStage() == ArenaStages.IN_PROCESS){
                arena.join(player);
            }
            return true;
        }

        ChatUtil.sendMessage(player, Messages.ArenaListNoArena);

        return true;
    }
}
