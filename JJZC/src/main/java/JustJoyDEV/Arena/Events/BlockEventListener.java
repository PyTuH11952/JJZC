package JustJoyDEV.Arena.Events;

import Arena.ArenaList;
import Arena.CustomAnvil;
import Arena.CustomBlock;
import Utils.ChatUtil;
import Utils.KeyUtil;
import com.mimikcraft.mcc.Main;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.sevents.events.player.click.right.PlayerRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import java.util.ArrayList;
import java.util.List;

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
                ConfigurationSection locationSection = config.getConfigurationSection(locationName);
                List<String> tempList = new ArrayList<>();
                List<String> tempList2 = locationSection.getStringList("chests");
                for(String chestCords : tempList2){
                    tempList.add(chestCords);
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
        if(e.getBlock() == null){
            return;
        }
        if (!ArenaList.hasArena(e.getPlayer())){
            return;
        }

        for(CustomBlock customBlock : ArenaList.get(e.getPlayer()).getLocation().getCustomBlocks()){
            if(customBlock.location.equals(e.getBlock().getLocation())){
                if(customBlock.action == "anvil"){
                    CustomAnvil customAnvil = (CustomAnvil) customBlock;
                    if((int)(Math.random() * 2) > 0){
                        switch(customAnvil.getLevel()){
                            case 1:
                                e.getBlock().setType(Material.CHIPPED_ANVIL);
                                break;
                            case 2:
                                e.getBlock().setType(Material.DAMAGED_ANVIL);
                                break;
                            case 3:
                                e.getBlock().setType(Material.AIR);
                                Bukkit.getWorld(e.getPlayer().getUniqueId()).playSound(e.getBlock().getLocation(), Sound.BLOCK_ANVIL_PLACE ,1, 1);
                        }
                        customAnvil.onBreak();
                    }
                }
                customBlock.onClick(e.getPlayer());
            }
        }

        Location lowerBlockLocation = e.getBlock().getLocation();
        lowerBlockLocation.setY(e.getBlock().getLocation().getBlockY() - 1);
        if(ArenaList.get(e.getPlayer()).getLocation().getDoors().containsKey(e.getBlock().getLocation())
                || ArenaList.get(e.getPlayer()).getLocation().getDoors().containsKey(lowerBlockLocation)){
            if(ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(e.getPlayer().getInventory().getItemInMainHand()).isPresent()){
                if(ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(e.getPlayer().getInventory().getItemInMainHand()).get().getId().equals("lom")){
                    if(Math.random() * 10 > 5){
                        e.getPlayer().getWorld().getBlockAt(e.getBlock().getLocation()).setType(Material.AIR);
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1, 1);
                        ChatUtil.sendMessage(e.getPlayer(), "&eДверь открыта!");
                        if(ArenaList.get(e.getPlayer()).getLocation().getDoors().get(e.getBlock().getLocation()) != null){
                            ArenaList.get(e.getPlayer()).getGame().spawnRandomArtifact(ArenaList.get(e.getPlayer()).getLocation().getDoors().get(e.getBlock().getLocation()));
                        }else if(ArenaList.get(e.getPlayer()).getLocation().getDoors().get(lowerBlockLocation) != null){
                            ArenaList.get(e.getPlayer()).getGame().spawnRandomArtifact(ArenaList.get(e.getPlayer()).getLocation().getDoors().get(lowerBlockLocation));
                        }
                    }else{
                        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1, 1);
                        ChatUtil.sendMessage(e.getPlayer(), "&cНе удалось открыть дверь");
                    }
                    ItemStack air = new ItemStack(Material.AIR, 1);
                    e.getPlayer().getInventory().setItemInMainHand(air);
                }else if(ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(e.getPlayer().getInventory().getItemInMainHand()).get().getId().equals("lom2")){
                    e.getPlayer().getWorld().getBlockAt(e.getBlock().getLocation()).setType(Material.AIR);
                    if(ArenaList.get(e.getPlayer()).getLocation().getDoors().get(e.getBlock().getLocation()) != null){
                        ArenaList.get(e.getPlayer()).getGame().spawnRandomArtifact(ArenaList.get(e.getPlayer()).getLocation().getDoors().get(e.getBlock().getLocation()));
                    }else if(ArenaList.get(e.getPlayer()).getLocation().getDoors().get(lowerBlockLocation) != null){
                        ArenaList.get(e.getPlayer()).getGame().spawnRandomArtifact(ArenaList.get(e.getPlayer()).getLocation().getDoors().get(lowerBlockLocation));
                    }
                    ItemStack air = new ItemStack(Material.AIR, 1);
                    e.getPlayer().getInventory().setItemInMainHand(air);
                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1, 1);
                    ChatUtil.sendMessage(e.getPlayer(), "&eДверь открыта!");
                }
            }else {
                ChatUtil.sendMessage(e.getPlayer(), "&cНеобходим лом!");
            }
        }
    }

}

