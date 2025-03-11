package Utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RemoveItemUtil {
    public static void remove(Player p, ItemStack s, int c) {
        for (int i = 0; i < p.getInventory().getSize(); i++) {
            ItemStack stack = p.getInventory().getItem(i);
            if (stack == null)
                continue;
            if (stack.getItemMeta().equals(s.getItemMeta())) {
                if (stack.getAmount() == 0)
                    break;
                if (stack.getAmount() <= c) {
                    c = c - stack.getAmount();
                    stack.setAmount(-1);
                }
                if (stack.getAmount() > c) {
                    stack.setAmount(stack.getAmount() - c);
                    c = 0;
                }
            }
        }
    }
}
