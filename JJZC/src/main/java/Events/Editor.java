package Events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Editor{
    public Player player;
    public String locName;
    public int stage;
    public Map<Location, Material> changes;
    public Editor(Player player, String locName, int stage){
        this.player = player;
        this.locName = locName;
        this.stage = stage;
        changes = new HashMap<>();
    }
}
