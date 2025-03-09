package Arena;

import Utils.ChatUtil;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomAnvil extends CustomBlock {
    public int level = 1;
    public CustomAnvil(Location location, String action) {
        super(location, action);
    }
    @Override
    public void onClick(Player player){
        int materialCount = 0;
        for(ItemStack itemStack : player.getInventory()){
            if(ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(itemStack).isPresent()){
                if(ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(itemStack).get().getId().equals("material5")){
                    materialCount += itemStack.getAmount();
                    if(materialCount >= 5 * level){
                        ItemStack itemToRemove = new ItemStack(itemStack);
                        itemToRemove.setAmount(5);
                        player.getInventory().remove(itemToRemove);
                        Location artLocation = location;
                        artLocation.setY(artLocation.getY() + 1);
                        ArenaList.get(player).getGame().spawnRandomArtifact(artLocation);
                        return;
                    }
                }
            }
        }
        ChatUtil.sendMessage(player, "&cНедостаточно материалов!");
    }

    public void onBreak(){
        level++;
    }
}
