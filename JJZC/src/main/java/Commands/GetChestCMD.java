package Commands;

import Utils.ChatUtil;
import Utils.KeyUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class GetChestCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;
        if (!player.isOp()) {
            ChatUtil.sendMessage(player, "&cУ вас нет прав на эту команду!");
            return true;
        }
        if (args.length == 0) {
            ChatUtil.sendMessage(player, "&cВвдеите тип предмета!");
            return true;
        }
        if (args.length == 1) {
            ChatUtil.sendMessage(player, "&cВвдеите название локации!");
            return true;
        }
        if(!args[0].equalsIgnoreCase("chest") && !args[0].equalsIgnoreCase("barrel")){
            ChatUtil.sendMessage(player, "&cНеверный тип предмета!");
            return true;
        }
        ItemStack item = new ItemStack(Material.valueOf(args[0].toUpperCase()));
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("Локация: " + args[1]);
        itemMeta.getPersistentDataContainer().set(KeyUtil.locationKey, PersistentDataType.STRING, "location");
        item.setItemMeta(itemMeta);
        player.getInventory().setItemInMainHand(item);

        return true;
    }
}