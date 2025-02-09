package Commands;

import Utils.ChatUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GetChestsCMD implements CommandExecutor {
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
        List<String> lore = new ArrayList<>();
        lore.add(0, args[1]);
        Material material = Material.getMaterial(args[0]);
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("cords");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        player.getInventory().setItemInMainHand(item);


        return true;
    }
}