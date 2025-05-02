package JustJoyDEV.Arena.Commands;

import Events.BlockEventListener;
import Events.Editor;
import Utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChooseStructureChangesCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player)){
            System.out.println("Иди нафиг, консоль!");
            return true;
        }
        Player player = (Player) commandSender;
        if(args.length == 0){
            ChatUtil.sendMessage(player, "&cВведите название локации!");
             return true;
        }
        if(args.length == 1){
            ChatUtil.sendMessage(player, "&cВведите номер стадии!");
            return true;
        }
        BlockEventListener.editors.add(new Editor(player, args[0], Integer.parseInt(args[1])));
        return true;
    }
}
