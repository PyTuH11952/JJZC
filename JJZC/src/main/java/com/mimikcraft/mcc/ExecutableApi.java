package com.mimikcraft.mcc;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import com.ssomar.score.api.executableitems.events.AddItemInPlayerInventoryEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ExecutableApi {
    public static void giveExecutableItem(Player player, String executableItemId, int amount) {
        ItemStack item = null;
        Optional<ExecutableItemInterface> eiOpt = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(executableItemId);
        if (eiOpt.isPresent()) {
            item = eiOpt.get().buildItem(amount, Optional.empty(), Optional.of(player));
        }
        if (item != null) {
            AddItemInPlayerInventoryEvent eventToCall = new AddItemInPlayerInventoryEvent(player, item, player.getInventory().firstEmpty());
            Bukkit.getPluginManager().callEvent(eventToCall);
            player.getInventory().addItem(item);
        }
    }
}
