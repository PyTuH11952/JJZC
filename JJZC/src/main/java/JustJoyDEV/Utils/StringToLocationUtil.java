package JustJoyDEV.Utils;

import org.bukkit.Location;
import org.bukkit.World;

public class StringToLocationUtil {
    public static Location getLocation(World world, String[] cordsStr){
        double x = Double.parseDouble(cordsStr[0]);
        double y = Double.parseDouble(cordsStr[1]);
        double z = Double.parseDouble(cordsStr[2]);
        return new Location(world, x, y, z);
    }
}
