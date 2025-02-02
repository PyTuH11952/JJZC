package Commands;

import Arena.ArenaList;
import Arena.ArenaLocation;
import Arena.ArenaStages;
import Arena.Arena;
import Utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LaunchCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) return true;
        Player player = (Player) commandSender;
        Arena arena = ArenaList.get(args[0]);
        ArenaLocation.LocationTypes locationtype = ArenaLocation.LocationTypes.valueOf(args[1]);

        if (arena.getArenaStage() == ArenaStages.CLOSED && arena.getArenaStage() != ArenaStages.RESET){
            arena.setLocationType(locationtype);
            arena.setArenaStage(ArenaStages.WAITING);
            arena.join(player);
            ChatUtil.sendMessage(player, "&aАрена &f" + args[0] + " &7запущена на локации &f" + locationtype);
            return true;
        } else {
            ChatUtil.sendMessage(player, "Данная арена недоступна!");
            return true;
        }
    }
}
