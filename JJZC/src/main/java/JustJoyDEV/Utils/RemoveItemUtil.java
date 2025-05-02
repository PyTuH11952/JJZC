package JustJoyDEV.Utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RemoveItemUtil {
    public static void remove(Player player, ItemStack item, int count) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack == null)
                continue;
            if (stack.getItemMeta().equals(item.getItemMeta())) {
                if (stack.getAmount() == 0)
                    break;
                if (stack.getAmount() <= count) {
                    count = count - stack.getAmount();
                    stack.setAmount(-1);
                }
                if (stack.getAmount() > count) {
                    stack.setAmount(stack.getAmount() - count);
                    count = 0;
                }
            }
        }
    }
}
