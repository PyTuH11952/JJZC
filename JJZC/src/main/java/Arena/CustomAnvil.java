package Arena;

import Utils.ChatUtil;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CustomAnvil extends CustomBlock {
    int level = 1;
    public CustomAnvil(Location location, String action) {
        super(location, action);
    }
    @Override
    public void onClick(Player buyer){
        int materialCount = 0;
        List<Integer> indexes = new ArrayList<>();
        for(int i = 0; i < buyer.getInventory().getSize(); i++){
            ItemStack itemStack = buyer.getInventory().getItem(i);
            if(ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(itemStack).isPresent()){
                if(ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(itemStack).get().getId().equals("material5")){
                    materialCount += itemStack.getAmount();
                    if(itemStack.getAmount() > 5 * level){
                        itemStack.setAmount(itemStack.getAmount() - 5 * level);
                        Location artLocation = location;
                        artLocation.setY(artLocation.getY() + 1);
                        ArenaList.get(buyer).getGame().spawnRandomArtifact(artLocation);
                        return;
                    }
                    if(itemStack.getAmount() == 5 * level){
                        buyer.getInventory().getItem(i).setType(Material.AIR);
                        Location artLocation = location;
                        artLocation.setY(artLocation.getY() + 1);
                        ArenaList.get(buyer).getGame().spawnRandomArtifact(artLocation);
                        return;
                    }
                    indexes.add(i);
                    if(materialCount >= 5 * level){
                        int removedItemsCount = 0;
                        for(int index : indexes){
                            ItemStack itemToRemove = buyer.getInventory().getItem(index);
                            if(buyer.getInventory().getItem(index).getAmount() <= 5 * level - removedItemsCount) {
                                itemToRemove.setType(Material.AIR);
                            }else{
                                itemToRemove.setAmount(itemToRemove.getAmount() - 5 * level + removedItemsCount);
                            }
                        }
                        ItemStack itemToRemove = new ItemStack(itemStack);
                        itemToRemove.setAmount(5);
                        buyer.getInventory().remove(itemToRemove);
                        Location artLocation = location;
                        artLocation.setY(artLocation.getY() + 1);
                        ArenaList.get(buyer).getGame().spawnRandomArtifact(artLocation);
                        return;
                    }
                }
            }
        }
        ChatUtil.sendMessage(buyer, "&cНедостаточно материала!");
    }

    public void onBreak(){
        level++;
    }

    public int getLevel(){return level;}
}
