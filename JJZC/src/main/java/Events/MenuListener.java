package Events;

import Arena.Arena;
import Arena.ArenaList;
import Arena.ArenaStages;
import Arena.ArenaLocation;
import Utils.ChatUtil;
import Utils.KeyUtil;
import com.mimikcraft.mcc.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getItem(e.getSlot()) == null) {
            return;
        }
        if (!e.getInventory().getItem(e.getSlot()).hasItemMeta()) {
            return;
        }
        if (e.getInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(KeyUtil.buttonKey, PersistentDataType.STRING) == null) {
            return;
        }
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        if (e.getInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(KeyUtil.buttonKey, PersistentDataType.STRING).equals("exit")) {
            Arena arena = ArenaList.get(player);
            arena.leave(player);
        }
        else if (e.getInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(KeyUtil.buttonKey, PersistentDataType.STRING).equals("join")) {
            if (e.getInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(KeyUtil.arenaKey, PersistentDataType.STRING) == null) {
                return;
            }
            ArenaList.get(e.getInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(KeyUtil.arenaKey, PersistentDataType.STRING)).join((Player) e.getWhoClicked());
        }
        else if (e.getInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(KeyUtil.buttonKey, PersistentDataType.STRING).equals("refresh")) {
            Inventory menu = Bukkit.createInventory(null, 45);
            List<Arena> availableArenas = ArenaList.getAvailable(player);
            int offset = availableArenas.size() % 2 == 0 ? (9 - availableArenas.size() + 1) / 2 + 18 : (9 - availableArenas.size()) / 2 + 18;
            for(int i = 0; i < availableArenas.size(); i++){
                ItemStack joinBtn = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
                ItemMeta joinBtnItemMeta = joinBtn.getItemMeta();
                joinBtnItemMeta.getPersistentDataContainer().set(KeyUtil.buttonKey, PersistentDataType.STRING, "join");
                joinBtnItemMeta.getPersistentDataContainer().set(KeyUtil.arenaKey, PersistentDataType.STRING, availableArenas.get(i).getName());
                joinBtnItemMeta.setDisplayName("Арена " + (i + 1));
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
                        hardLevelStr = "&5экстримальный";
                }
                lore.add(ChatColor.translateAlternateColorCodes('&', "&7Уровень сложности: &6" + hardLevelStr));
                String gameType = availableArenas.get(i).getGame().isGameInfinity() ? "обычный" : "бесконечный";
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
            refreshBtnItemMeta.setDisplayName("Обновить");
            refreshBtnItemMeta.getPersistentDataContainer().set(KeyUtil.buttonKey, PersistentDataType.STRING, "refresh");
            refreshBtn.setItemMeta(refreshBtnItemMeta);
            menu.setItem(36, refreshBtn);

            ItemStack forceStartBtn = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE, 1);
            ItemMeta forceStartBtnItemMeta = forceStartBtn.getItemMeta();
            forceStartBtnItemMeta.setDisplayName("&eБыстрый страт");
            forceStartBtnItemMeta.getPersistentDataContainer().set(KeyUtil.buttonKey, PersistentDataType.STRING, "forceStart");
            forceStartBtn.setItemMeta(forceStartBtnItemMeta);
            menu.setItem(39, forceStartBtn);
            player.openInventory(menu);

            ItemStack createGameBtn = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
            ItemMeta createGameBtnItemMeta = createGameBtn.getItemMeta();
            createGameBtnItemMeta.setDisplayName("&2Создать новую игру");
            createGameBtnItemMeta.getPersistentDataContainer().set(KeyUtil.buttonKey, PersistentDataType.STRING, "createGame");
            createGameBtn.setItemMeta(createGameBtnItemMeta);
            menu.setItem(41, createGameBtn);
            player.openInventory(menu);
        }
        else if (e.getInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(KeyUtil.buttonKey, PersistentDataType.STRING).equals("forceStart")) {
            List<Arena> availableArenas = ArenaList.getAvailable(player);
            for(Arena arena: availableArenas){
                if(arena.getArenaStage() == ArenaStages.STARTING){
                    arena.join(player);
                    return;
                }
            }

            for(Arena arena: availableArenas){
                arena.join(player);
                return;
            }

            ChatUtil.sendMessage(player, "&cНет подходящих арен!");
        }
        else if (e.getInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(KeyUtil.buttonKey, PersistentDataType.STRING).equals("createGame")) {
            if(ArenaList.getFreeArena() == null){
                ChatUtil.sendMessage(player, "&Нет свободных арен!");
            }

            Arena arena = ArenaList.getFreeArena();

            File folder = new File(Main.getInstance().getDataFolder().getAbsolutePath());

            File file = new File(folder.getAbsolutePath() + "/Locations.yml");

            if (!file.exists()) {
                try {
                    file.createNewFile();
                    ChatUtil.sendMessage(player, "&cЧто-то пошло не так!");
                    return;
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            Inventory menu = Bukkit.createInventory(null, 9);
            List<ArenaLocation> availableLocations = new ArrayList<>();
            for(String location: config.getKeys(false)){
                if(!player.hasPermission("loc" + ArenaLocation.LocationTypes.valueOf(location.toUpperCase()) + ".1") && !location.equals("hospital")){
                    break;
                }
                availableLocations.add(new ArenaLocation(ArenaLocation.LocationTypes.valueOf(location.toUpperCase()), arena.getArenaWorld()));
            }
            int offset = availableLocations.size() % 2 == 0 ? (9 - availableLocations.size() + 1) / 2 + 18 : (9 - availableLocations.size()) / 2 + 18;
            for(int i = 0; i < availableLocations.size(); i++){
                ConfigurationSection locationSection = config.getConfigurationSection(availableLocations.get(i).getLocationType().name().toLowerCase());
                ItemStack locationBtn = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
                ItemMeta locationItemMeta = locationBtn.getItemMeta();
                locationItemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', availableLocations.get(i).getName()));
                locationItemMeta.setLore(locationSection.getStringList("lore"));
                locationItemMeta.getPersistentDataContainer().set(KeyUtil.buttonKey, PersistentDataType.STRING, "location");
                locationItemMeta.getPersistentDataContainer().set(KeyUtil.locationKey, PersistentDataType.STRING, locationSection.getName());
                locationBtn.setItemMeta(locationItemMeta);
                menu.setItem(offset + i, locationBtn);
            }

            player.openInventory(menu);
        }
        else if (e.getInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(KeyUtil.buttonKey, PersistentDataType.STRING).equals("location")){
            if (e.getInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(KeyUtil.locationKey, PersistentDataType.STRING) == null){
                return;
            }
            if(ArenaList.getFreeArena() == null){
                player.closeInventory();
                ChatUtil.sendMessage(player, "&cАрену уже заняли!");
            }
            Arena arena = ArenaList.getFreeArena();
            arena.setLocationType(ArenaLocation.LocationTypes.valueOf(e.getInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(KeyUtil.locationKey, PersistentDataType.STRING).toUpperCase()));
            arena.join(player);
        }
    }
}
