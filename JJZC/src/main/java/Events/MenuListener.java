package Events;

import Arena.Arena;
import Arena.ArenaList;
import Utils.KeyUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getInventory().getItem(e.getSlot()) == null){
            return;
        }
        if(!e.getInventory().getItem(e.getSlot()).hasItemMeta()){
            return;
        }
        if(e.getInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(KeyUtil.buttonKey, PersistentDataType.STRING) == null){
            return;
        }
        if(e.getInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(KeyUtil.buttonKey, PersistentDataType.STRING).equals("exit")){
            e.setCancelled(true);
            Player player = (Player)e.getWhoClicked();
            Arena arena = ArenaList.get(player);
            arena.leave(player);
        }else if(e.getInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(KeyUtil.buttonKey, PersistentDataType.STRING).equals("join")){
            e.setCancelled(true);
            if(e.getInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(KeyUtil.arenaKey, PersistentDataType.STRING) == null){
                return;
            }
            ArenaList.get(e.getInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(KeyUtil.arenaKey, PersistentDataType.STRING)).join((Player)e.getWhoClicked());
        }else if(e.getInventory().getItem(e.getSlot()).getItemMeta().getPersistentDataContainer().get(KeyUtil.buttonKey, PersistentDataType.STRING).equals("refresh")){
            Inventory menu = Bukkit.createInventory(null, 45);
            List<Arena> availableArenas = ArenaList.getAvailable((Player) e.getWhoClicked());
            int offset = availableArenas.size() % 2 == 0 ? (9 - availableArenas.size() + 1) / 2  + 18: (9 - availableArenas.size()) / 2 + 18;
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
            ((Player) e.getWhoClicked()).openInventory(menu);
        }
    }
}
