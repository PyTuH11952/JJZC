package Commands;

import Arena.Arena;
import Arena.ArenaList;
import Utils.ChatUtil;
import Utils.KeyUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;


public class JoinCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player)) return true;

        Player player = (Player) commandSender;

        if (args.length == 0) {
            Inventory menu = Bukkit.createInventory(null, 45);
            List<Arena> availableArenas = ArenaList.getAvailable(player);
            int offset = availableArenas.size() % 2 == 0 ? (9 - availableArenas.size() + 1) / 2 + 18 : (9 - availableArenas.size()) / 2 + 18;
            for(int i = 0; i < availableArenas.size(); i++){
                ItemStack joinBtn = new ItemStack(Material.GREEN_STAINED_GLASS, 1);
                ItemMeta joinBtnItemMeta = joinBtn.getItemMeta();
                joinBtnItemMeta.getPersistentDataContainer().set(KeyUtil.buttonKey, PersistentDataType.STRING, "join");
                joinBtnItemMeta.getPersistentDataContainer().set(KeyUtil.arenaKey, PersistentDataType.STRING, availableArenas.get(i).getName());
                joinBtnItemMeta.setDisplayName("Арена " + (i + 1));
                List<String> lore = new ArrayList<>();
                lore.add("Локация: " + availableArenas.get(i).getLocation().getLocationType().name());
                lore.add("Этап: " + availableArenas.get(i).getGame().stage);
                lore.add("Волна: " + availableArenas.get(i).getGame().wave);
                joinBtnItemMeta.setLore(lore);
                joinBtn.setItemMeta(joinBtnItemMeta);
                menu.setItem(offset + i, joinBtn);
            }
            ItemStack refreshBtn = new ItemStack(Material.GRAY_STAINED_GLASS, 1);
            ItemMeta refreshBtnItemMeta = refreshBtn.getItemMeta();
            refreshBtnItemMeta.setDisplayName("Обновить");
            refreshBtnItemMeta.getPersistentDataContainer().set(KeyUtil.buttonKey, PersistentDataType.STRING, "refresh");
            refreshBtn.setItemMeta(refreshBtnItemMeta);
            menu.setItem(36, refreshBtn);
            player.openInventory(menu);
            return  true;
        }

        if (!ArenaList.hasArena(args[0])) {
            ChatUtil.sendMessage(player, "&cТакой арены нет!");
            return true;
        }
        Arena arena = ArenaList.get(args[0]);
        arena.join(player);
        return true;
    }
}