package JustJoyDEV.Arena.Commands;

import Arena.Arena;
import Arena.ArenaList;
import Arena.ArenaLocation;
import Utils.ChatUtil;
import Utils.KeyUtil;
import com.mimikcraft.mcc.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LaunchCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) return true;
        Player player = (Player) commandSender;
        if(ArenaList.getFreeArena() == null){
            ChatUtil.sendMessage(player, "&Нет свободных арен!");
            return true;
        }
        Arena arena = ArenaList.getFreeArena();

        File folder = new File(Main.getInstance().getDataFolder().getAbsolutePath());

        File file = new File(folder.getAbsolutePath() + "/Locations.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
                ChatUtil.sendMessage(player, "&cЧто-то пошло не так!");
                return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
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
        int offset = availableLocations.size() % 2 == 0 ? (9 - availableLocations.size() + 1) / 2: (9 - availableLocations.size()) / 2;
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
        return true;
    }
}
