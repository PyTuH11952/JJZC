package Events;

import com.mimikcraft.mcc.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Material.BARREL;

public class BlockEventListener implements Listener {
    List<Location> chests = new ArrayList<>();
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (event.getBlock().getType() == Material.CHEST || event.getBlock().getType() == BARREL){

            if (item.getItemMeta().getDisplayName().equalsIgnoreCase("cords")){
                String location = item.getItemMeta().getLore().get(0);

                Location chest = event.getBlock().getLocation();
                chests.add(chest);

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

                if(config.getConfigurationSection(location) == null){
                    config.createSection(location);
                }
                if(config.getConfigurationSection(location + ".chests") == null){
                    config.createSection(location + ".chests");
                }
                List<String> locationsStr = new ArrayList<>();
                for(Location configchest : chests){
                    String res = configchest.getX() + " " + configchest.getY() + " " + configchest.getZ();
                    locationsStr.add(res);
                }
                config.set(location + ".chests", locationsStr);
                try {
                    config.save(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }




        }
    }
}
