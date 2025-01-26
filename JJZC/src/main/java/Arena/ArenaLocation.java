package Arena;

import com.mimikcraft.mcc.Main;
import io.lumine.mythic.bukkit.utils.config.ConfigurationSection;
import io.lumine.mythic.bukkit.utils.config.file.YamlConfiguration;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

public class ArenaLocation {

    public LocationTypes locationType;

    private Location spawnPosition;
    private final List<Location> chests = new ArrayList<>();
    private final Map<String, Double> zombies = new HashMap<>();
    private final List<Stage> stages = new ArrayList<>();

    public ArenaLocation(LocationTypes locationType, World world){
        File folder = new File(Main.getInstance().getDataFolder().getAbsolutePath());

        File file = new File(folder.getAbsolutePath() + "/Locations.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection locationSection = config.getConfigurationSection(locationType.toString().toLowerCase());
        String[] coordinatesStr = locationSection.getString("spawnLocation").split(" ");
        double spawnX = Double.parseDouble(coordinatesStr[0]);
        double spawnY = Double.parseDouble(coordinatesStr[1]);
        double spawnZ = Double.parseDouble(coordinatesStr[2]);
        spawnPosition = new Location(world, spawnX, spawnY, spawnZ);
        List<String> chestsCoordinatesStr = locationSection.getStringList("chests");
        for(String chestCord : chestsCoordinatesStr){
            String[] chestCordsStr = chestCord.split(" ");
            double x = Double.parseDouble(chestCordsStr[0]);
            double y = Double.parseDouble(chestCordsStr[1]);
            double z = Double.parseDouble(chestCordsStr[2]);
            chests.add(new Location(world, x, y, z));
        }

        for(Map.Entry<String, Object> entry: locationSection.getValues(false).entrySet()){
            zombies.put(entry.getKey(), Double.parseDouble(entry.getValue().toString()));
        }
    }

    public Location getSpawnPosition() {
        return spawnPosition;
    }

    public List<Location> getChests() {
        return chests;
    }

    public Map<String, Double> getZombies() {
        return zombies;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public enum LocationTypes {
        HOSPITAL,
        MALL,
        GARAGE,
        FACTORY,
        METRO
    }
}


class Stage{
    Location[] spawners;
    int wavesCount;
    public Stage(Location[] spawners, int wavesCount){
        this.spawners = spawners;
        this.wavesCount = wavesCount;
    }
}


