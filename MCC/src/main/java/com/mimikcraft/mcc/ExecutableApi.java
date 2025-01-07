package com.mimikcraft.mcc;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ExecutableApi {
    public static void giveExecutableItem(Player player, String executableItemId, int amount) {
        ItemStack item = null;
        Optional<ExecutableItemInterface> eiOpt = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(executableItemId);
        if (eiOpt.isPresent()) item = eiOpt.get().buildItem(amount, Optional.empty(), Optional.of(player));
        if (item != null)
            player.getInventory().addItem(item);
    }
}
