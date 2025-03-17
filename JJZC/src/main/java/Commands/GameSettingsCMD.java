package Commands;

import Arena.ArenaList;
import Arena.Arena;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GameSettingsCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)) return true;
        Player player = (Player) commandSender;
        if(ArenaList.get(player) == null){
            ChatUtil.sendMessage(player, "&cВы находитесь не на арене!");
            return true;
        }
        Arena arena = ArenaList.get(player);
        if(!arena.getHost().getUniqueId().toString().equals(player.getUniqueId().toString())){
            ChatUtil.sendMessage(player, "&cКоманда доступна только хосту!");
        }

        Inventory menu = Bukkit.createInventory(null, 9);
        List<ItemStack> hardLevelsBtns = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            if(player.hasPermission("loc" + (arena.getLocation().getLocationType().ordinal() + 1) + "." + (i + 1))){
                String hardLevelStr = "";
                Material hardLevelBtnMaterial;
                switch (i){
                    case 0:
                        if(arena.getGame().getHardLevel() == i + 1){
                            hardLevelStr = "&7Лёгкий";
                            hardLevelBtnMaterial = Material.GRAY_STAINED_GLASS_PANE;
                            break;
                        }
                        hardLevelStr = "&aЛёгкий";
                        hardLevelBtnMaterial = Material.GREEN_STAINED_GLASS_PANE;
                        break;
                    case 1:
                        if(arena.getGame().getHardLevel() == i + 1){
                            hardLevelStr = "&7Нормальный";
                            hardLevelBtnMaterial = Material.GRAY_STAINED_GLASS_PANE;
                            break;
                        }
                        hardLevelStr = "&eНормальный";
                        hardLevelBtnMaterial = Material.YELLOW_STAINED_GLASS_PANE;
                        break;
                    case 2:
                        if(arena.getGame().getHardLevel() == i + 1){
                            hardLevelStr = "&7Сложный";
                            hardLevelBtnMaterial = Material.GRAY_STAINED_GLASS_PANE;
                            break;
                        }
                        hardLevelStr = "&cСложный";
                        hardLevelBtnMaterial = Material.RED_STAINED_GLASS_PANE;
                        break;
                    default:
                        if(arena.getGame().getHardLevel() == i + 1){
                            hardLevelStr = "&7Экстримальный";
                            hardLevelBtnMaterial = Material.GRAY_STAINED_GLASS_PANE;
                            break;
                        }
                        hardLevelStr = "&5Экстримальный";
                        hardLevelBtnMaterial = Material.PURPLE_STAINED_GLASS_PANE;
                }
                ItemStack hardLevelBtn = new ItemStack(hardLevelBtnMaterial, 1);
                ItemMeta hardLevelBtnItemMeta = hardLevelBtn.getItemMeta();
                hardLevelBtnItemMeta.setDisplayName(hardLevelStr);
                if(hardLevelStr.startsWith("&7")){
                    List<String> lore = new ArrayList<>();
                    lore.add("&o&7Выбрана эта сложность");
                    hardLevelBtnItemMeta.setLore(lore);
                }
                hardLevelBtnItemMeta.getPersistentDataContainer().set(KeyUtil.buttonKey, PersistentDataType.STRING, "hardLevel");
                hardLevelBtnItemMeta.getPersistentDataContainer().set(KeyUtil.hardLevelKey, PersistentDataType.INTEGER, i + 1);
                hardLevelBtn.setItemMeta(hardLevelBtnItemMeta);
                menu.setItem(36, hardLevelBtn);
                hardLevelsBtns.add(hardLevelBtn);
                continue;
            }
            break;
        }

        int offset = hardLevelsBtns.size() % 2 == 0 ? (9 - hardLevelsBtns.size() + 1) / 2: (9 - hardLevelsBtns.size()) / 2;

        for(int i = 0; i < hardLevelsBtns.size(); i++){
            menu.setItem(offset + i, hardLevelsBtns.get(i));
        }

        ItemStack infinityModeBtn = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta infinityModeBtnItemMeta = infinityModeBtn.getItemMeta();
        infinityModeBtnItemMeta.setDisplayName("&5Бесконечныый режим");
        infinityModeBtnItemMeta.getPersistentDataContainer().set(KeyUtil.buttonKey, PersistentDataType.STRING, "infinityMode");
        infinityModeBtn.setItemMeta(infinityModeBtnItemMeta);
        menu.setItem(7, infinityModeBtn);

        player.openInventory(menu);
        return true;
    }
}
