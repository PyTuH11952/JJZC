package Commands;

import Arena.ArenaList;
import Arena.ArenaLocation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TestCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        ArenaList.get(args[0]).setLocationType(ArenaLocation.LocationTypes.valueOf(args[1]));
        return true;
    }
}
