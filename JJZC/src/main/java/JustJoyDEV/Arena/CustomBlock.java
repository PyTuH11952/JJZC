package JustJoyDEV.Arena;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CustomBlock {
    public Location location;
    public String action;

    public CustomBlock(Location location, String action){
        this.location = location;
        this.action = action;
    }

    public void onClick(Player player){
        if(action.startsWith("/")){
            player.performCommand(action.substring(1));
        }
    }
}
