package Events;

import Arena.Arena;
import Arena.ArenaList;
import Utils.ChatUtil;
import Utils.KeyUtil;
import com.mimikcraft.mcc.ExecutableApi;
import com.mimikcraft.mcc.Main;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemsManagerInterface;
import com.ssomar.sevents.events.player.click.right.PlayerRightClickEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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
import java.util.*;

public class BlockEventListener implements Listener {

    public static List<Editor> editors = new ArrayList<>();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        for(Editor editor : editors){
            if(editor.player.getUniqueId().toString().equals(player.getUniqueId().toString())){
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

    @EventHandler
    public void onRightClikEvent(PlayerRightClickEvent e){
        if(e.getBlock().getType().isAir()){
            return;
        }
        Location lowerBlockLocation = e.getBlock().getLocation();
        lowerBlockLocation.setY(e.getBlock().getLocation().getBlockY() - 1);
        if(ArenaList.get(e.getPlayer()).getLocation().getDoors().containsKey(e.getBlock().getLocation())
                || ArenaList.get(e.getPlayer()).getLocation().getDoors().containsKey(lowerBlockLocation)){
            if(ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(e.getPlayer().getInventory().getItemInMainHand()).isPresent()){
                if(ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(e.getPlayer().getInventory().getItemInMainHand()).get().getId().equals("lom")){
                    if(Math.random() * 10 > 5){
                        e.getPlayer().getWorld().getBlockAt(e.getBlock().getLocation()).setType(Material.AIR);
                        if(ArenaList.get(e.getPlayer()).getLocation().getDoors().get(e.getBlock().getLocation()) != null){
                            spawnRandomArtifact(ArenaList.get(e.getPlayer()).getLocation().getDoors().get(e.getBlock().getLocation()), ArenaList.get(e.getPlayer()));
                        }else if(ArenaList.get(e.getPlayer()).getLocation().getDoors().get(lowerBlockLocation) != null){
                            spawnRandomArtifact(ArenaList.get(e.getPlayer()).getLocation().getDoors().get(lowerBlockLocation), ArenaList.get(e.getPlayer()));
                        }
                    }
                    e.getPlayer().getInventory().remove(e.getPlayer().getInventory().getItemInMainHand());
                }else if(ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(e.getPlayer().getInventory().getItemInMainHand()).get().getId().equals("lom2")){
                    e.getPlayer().getWorld().getBlockAt(e.getBlock().getLocation()).setType(Material.AIR);
                    if(ArenaList.get(e.getPlayer()).getLocation().getDoors().get(e.getBlock().getLocation()) != null){
                        ArenaList.get(e.getPlayer()).getGame().spawnMob(ArenaList.get(e.getPlayer()).getLocation().getDoors().get(e.getBlock().getLocation()), "art");
                    }else if(ArenaList.get(e.getPlayer()).getLocation().getDoors().get(lowerBlockLocation) != null){
                        ArenaList.get(e.getPlayer()).getGame().spawnMob(ArenaList.get(e.getPlayer()).getLocation().getDoors().get(lowerBlockLocation), "art");
                    }
                    e.getPlayer().getInventory().remove(e.getPlayer().getInventory().getItemInMainHand());
                }
            }
        }
    }

    private void spawnRandomArtifact(Location location, Arena arena){
        File folder = new File(Main.getInstance().getDataFolder().getAbsolutePath());

        File file = new File(folder.getAbsolutePath() + "/Artifacts.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection artifactsSection = config.getConfigurationSection("Artifacts");
        Map<String, Double> artifacts = new HashMap<>();
        Set<String> artifactsNames = artifactsSection.getKeys(false);
        for(String artifact : artifactsNames){
            ConfigurationSection artifactSection = artifactsSection.getConfigurationSection(artifact);
            for(int i = 0; i < artifactSection.getDoubleList("levelsSpawnChances").size(); i++){
                artifacts.put(artifact + "_" + i + 1, artifactSection.getDoubleList("levelsSpawnChances").get(i));
            }
        }
        int random = (int)(Math.random() * 10000);
        int temp = 0;
        String artifactName = "";
        for(Map.Entry<String, Double> entry : artifacts.entrySet()){
            temp += (int) (entry.getValue() * 100);
            if(random <= temp){
                artifactName = entry.getKey();
                break;
            }
        }
        arena.getGame().spawnMob(location, artifactName);
    }
}

