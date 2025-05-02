package Commands;

import Arena.Arena;
import Arena.ArenaList;
import Arena.ArenaStages;
import Utils.ChatUtil;
import com.mimikcraft.mcc.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KickCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player))
            return true;
        Player player = (Player) commandSender;
        if(args.length == 0){
            ChatUtil.sendMessage(player, "&cВвдеите ник игрока!");
            return true;
        }
        if(ArenaList.hasArena(player)){
            Arena arena = ArenaList.get(player);
            if(arena.getArenaStage() == ArenaStages.IN_PROCESS){
                ChatUtil.sendMessage(player, Messages.cannotUseInGame);
            }
            if(arena.getHost() != player){
                ChatUtil.sendMessage(player, Messages.noPerm);
                return true;
            }
            arena.leave(Bukkit.getPlayer(args[0]));
            return true;
        }else{
            ChatUtil.sendMessage(player, Messages.notOnArena);
            return true;
        }
    }
}