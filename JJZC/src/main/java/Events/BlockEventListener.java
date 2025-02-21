package Events;

import Utils.KeyUtil;
import com.mimikcraft.mcc.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlockEventListener implements Listener {

    public static List<Editor> editors = new ArrayList<>();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        for(Editor editor : editors){
            if(editor.player.getUniqueId().equals(player.getUniqueId())){
                editors.get(editors.indexOf(editor)).changes.put(event.getBlockPlaced().getLocation(), event.getBlockPlaced().getType());
            }
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item.getItemMeta().getPersistentDataContainer() != null){
            ItemMeta meta = item.getItemMeta();
            if(meta.getPersistentDataContainer().has(KeyUtil.locationKey, PersistentDataType.STRING)){
                String locationName = meta.getPersistentDataContainer().get(KeyUtil.locationKey, PersistentDataType.STRING).toLowerCase();
                Location chestLocation = event.getBlockPlaced().getLocation();
                String res = chestLocation.getX() + " " + chestLocation.getY() + " " + chestLocation.getZ();

                File folder = new File(Main.getInstance().getDataFolder().getAbsolutePath());

                File file = new File(folder.getAbsolutePath() + "/Locations.yml");

                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

                if(config.getConfigurationSection(locationName) == null){
                    config.createSection(locationName);
                }
                List<String> tempList = new ArrayList<>();
                if(config.getConfigurationSection(locationName + ".chests") == null){
                    config.createSection(locationName + ".chests");
                }else{
                    tempList.addAll(config.getStringList(locationName + ".chests"));
                }
                tempList.add(res);
                config.set(locationName + ".chests", tempList);
                try {
                    config.save(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        for(Editor editor : editors){
            if(editor.player.getUniqueId().equals(player.getUniqueId())){
                editors.get(editors.indexOf(editor)).changes.put(event.getBlock().getLocation(), Material.AIR);
            }
        }
    }
}

