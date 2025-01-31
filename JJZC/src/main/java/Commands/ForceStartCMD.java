package Commands;

import Arena.Arena;
import Arena.ArenaList;
import Utils.ChatUtil;
import com.mimikcraft.mcc.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ForceStartCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player)) return true;

        Player player = (Player) commandSender;
        for (int i = 1; i <= Main.getInstance().getMaxarenas(); i++){
            Arena arena = ArenaList.get("arena"+i);
<<<<<<< HEAD
            if (!arena.isArenaClosed()) {
                if (arena.canJoin(player)) {
=======
            if (arena.isArenaClosed() == false) {
                if (arena.canjoin(player) == true) {
>>>>>>> eaddb9b0fee2b6ce317da6b237075598ea33c985
                    arena.join(player);
                    return true;
                }
            }
        }
        ChatUtil.sendMessage(player, "&cНе удалось найти свободной арены! Попробуйте позже.");
        return true;
    }
}