package Events;

import Arena.Arena;
import Arena.ArenaList;
import Utils.KeyUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

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
        }
    }
}
