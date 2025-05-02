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
                ItemStack joinBtn = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
                ItemMeta joinBtnItemMeta = joinBtn.getItemMeta();
                joinBtnItemMeta.getPersistentDataContainer().set(KeyUtil.buttonKey, PersistentDataType.STRING, "join");
                joinBtnItemMeta.getPersistentDataContainer().set(KeyUtil.arenaKey, PersistentDataType.STRING, availableArenas.get(i).getName());
                joinBtnItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7Арена " + (i + 1)));
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Игроки: &6" + availableArenas.get(i).getPlayers().size()));
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Локация: &6" + availableArenas.get(i).getLocation().getName()));
                String hardLevelStr = "";
                switch (availableArenas.get(i).getGame().getHardLevel()){
                    case 1:
                        hardLevelStr = "&aлёгкий";
                        break;
                    case 2:
                        hardLevelStr = "&eнормальный";
                        break;
                    case 3:
                        hardLevelStr = "&cсложный";
                        break;
                    default:
                        hardLevelStr = "&5экстремальный&7(" + availableArenas.get(i).getGame().getHardLevel() + ")";
                }
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Уровень сложности: " + hardLevelStr));
                String gameType;
                if (availableArenas.get(i).getGame().isGameInfinity()){
                    gameType = "бесконечный";
                } else {
                    gameType = "обычный";
                }
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Режим: &6" + gameType));

                if(availableArenas.get(i).getArenaStage() == ArenaStages.IN_PROCESS){
                    lore.add(ChatColor.translateAlternateColorCodes('&', "&7Этап: &6" + availableArenas.get(i).getGame().stage));
                    lore.add(ChatColor.translateAlternateColorCodes('&', "&7Волна: &6" + availableArenas.get(i).getGame().wave));
                }else{
                    lore.add(ChatColor.translateAlternateColorCodes('&', "&7&oИгра ещё не началась!"));
                }
                joinBtnItemMeta.setLore(lore);
                joinBtn.setItemMeta(joinBtnItemMeta);
                menu.setItem(offset + i, joinBtn);
            }
            ItemStack refreshBtn = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            ItemMeta refreshBtnItemMeta = refreshBtn.getItemMeta();
            refreshBtnItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aОбновить"));
            refreshBtnItemMeta.getPersistentDataContainer().set(KeyUtil.buttonKey, PersistentDataType.STRING, "refresh");
            refreshBtn.setItemMeta(refreshBtnItemMeta);
            menu.setItem(36, refreshBtn);

            ItemStack forceStartBtn = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE, 1);
            ItemMeta forceStartBtnItemMeta = forceStartBtn.getItemMeta();
            forceStartBtnItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eБыстрый страт"));
            forceStartBtnItemMeta.getPersistentDataContainer().set(KeyUtil.buttonKey, PersistentDataType.STRING, "forceStart");
            forceStartBtn.setItemMeta(forceStartBtnItemMeta);
            menu.setItem(39, forceStartBtn);
            player.openInventory(menu);

            ItemStack createGameBtn = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
            ItemMeta createGameBtnItemMeta = createGameBtn.getItemMeta();
            createGameBtnItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&2Создать новую игру"));
            createGameBtnItemMeta.getPersistentDataContainer().set(KeyUtil.buttonKey, PersistentDataType.STRING, "createGame");
            createGameBtn.setItemMeta(createGameBtnItemMeta);
            menu.setItem(41, createGameBtn);
            player.openInventory(menu);
            return  true;
        }

        if (!ArenaList.hasArena(args[0])) {
            ChatUtil.sendMessage(player, Messages.ArenaListNoArena);
            return true;
        }
        Arena arena = ArenaList.get(args[0]);
        arena.join(player);
        return true;
    }
}