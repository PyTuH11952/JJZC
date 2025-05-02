package JustJoyDEV.Arena.Commands;

import Arena.Arena;
import Arena.ArenaList;
import Arena.ArenaStages;
import Utils.ChatUtil;
import Utils.KeyUtil;
import com.mimikcraft.mcc.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LeaveCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(commandSender instanceof Player)) return true;

        Player player = (Player) commandSender;

        Arena arena = ArenaList.get(player);
        if (arena == null) {
            ChatUtil.sendMessage(player, Messages.notOnArena);
            return true;
        }

        if(ArenaList.get(player).getArenaStage() == ArenaStages.IN_PROCESS){
            Inventory menu = Bukkit.createInventory(
                    null,
                    27,
                    ChatColor.translateAlternateColorCodes('&', "&cВыход из игры")
            );

            ItemStack exitButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta itemMeta = exitButton.getItemMeta();
            itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cВыйти из игры"));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7&oВы уверены, что хотите выйти из игры?"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7&oПосле выхода из игры вы больше не сможете в неё зайти!"));
            itemMeta.setLore(lore);
            itemMeta.getPersistentDataContainer().set(KeyUtil.buttonKey, PersistentDataType.STRING, "exit");
            exitButton.setItemMeta(itemMeta);
            menu.setItem(13, exitButton);
            player.openInventory(menu);
            return true;
        }

        arena.leave(player);
        ChatUtil.sendMessage(player, "&cВы отключились от арены!");
        return true;
    }
}
