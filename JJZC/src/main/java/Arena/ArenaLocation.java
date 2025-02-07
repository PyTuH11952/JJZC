package Arena;

import com.mimikcraft.mcc.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ArenaLocation {

    private LocationTypes locationType;

    private Location spawnLocation;
    private Location lobbyLocation;
    private World world;
    private double locationFactor;
    private final List<Location> chests = new ArrayList<>();
    private final List<Zombie> zombies = new ArrayList<>();
    private final List<Stage> stages = new ArrayList<>();

    public ArenaLocation(LocationTypes locationType, World world){
        this.world = world;
        setLocationType(locationType);
    }

    public void setLocationType(LocationTypes locationType) {
        this.locationType = locationType;
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
        ConfigurationSection locationSection = config.getConfigurationSection(locationType.name().toLowerCase());

        String[] spawnCoordinatesStr = locationSection.getString("spawnLocation").split(" ");
        double spawnX = Double.parseDouble(spawnCoordinatesStr[0]);
        double spawnY = Double.parseDouble(spawnCoordinatesStr[1]);
        double spawnZ = Double.parseDouble(spawnCoordinatesStr[2]);
        spawnLocation = new Location(world, spawnX, spawnY, spawnZ);

        String[] lobbyCoordinatesStr = locationSection.getString("lobbyLocation").split(" ");
        double lobbyX = Double.parseDouble(lobbyCoordinatesStr[0]);
        double lobbyY = Double.parseDouble(lobbyCoordinatesStr[1]);
        double lobbyZ = Double.parseDouble(lobbyCoordinatesStr[2]);
        lobbyLocation = new Location(world, lobbyX, lobbyY, lobbyZ);

        locationFactor = Double.parseDouble(locationSection.getString("locationFactor"));

        List<String> chestsCoordinatesStr = locationSection.getStringList("chests");
        for(String chestCords : chestsCoordinatesStr){
            String[] chestCordsStr = chestCords.split(" ");
            double x = Double.parseDouble(chestCordsStr[0]);
            double y = Double.parseDouble(chestCordsStr[1]);
            double z = Double.parseDouble(chestCordsStr[2]);
            chests.add(new Location(world, x, y, z));
        }

        ConfigurationSection zombiesSection = locationSection.getConfigurationSection("zombies");
        Set<String> zombiesNames = zombiesSection.getKeys(false);
        for(String zombieName : zombiesNames){
            Zombie zombie = new Zombie(zombieName,
                    zombiesSection.getDouble(zombieName + ".spawnChance"),
                    zombiesSection.getInt(zombieName + ".hardLevel"));
            zombies.add(zombie);
        }

        ConfigurationSection stagesSection = locationSection.getConfigurationSection("stages");
        Set<String> stagesKeys = stagesSection.getKeys(false);
        for(String section : stagesKeys){
            List<String> spawnersCoordinatesStr = stagesSection.getStringList(section + ".spawners");
            List<Location> tempCordsList = new ArrayList<>();
            for(String spawnerCords : spawnersCoordinatesStr){
                String[] spawnerCordsStr = spawnerCords.split(" ");
                double x = Double.parseDouble(spawnerCordsStr[0]);
                double y = Double.parseDouble(spawnerCordsStr[1]);
                double z = Double.parseDouble(spawnerCordsStr[2]);
                tempCordsList.add(new Location(world, x, y, z));
            }
            int wavesCount = stagesSection.getInt(section + ".wavesCount");
            stages.add(new Stage(tempCordsList, wavesCount));
        }
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public Location getLobbyLocation(){
        return lobbyLocation;
    }

    public List<Location> getChests() {
        return chests;
    }

    public List<Zombie> getZombies() {
        return zombies;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public double getLocationFactor(){
        return locationFactor;
    }

    public LocationTypes getLocationType() {
        return locationType;
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
    List<Location> spawners;
    int wavesCount;
    public Stage(List<Location> spawners, int wavesCount){
        this.spawners = spawners;
        this.wavesCount = wavesCount;
    }
}


class Zombie{
    String name;
    Double spawnChance;
    int hardLevel;
    public Zombie(String name, Double spawnChance, int hardLevel){
        this.name = name;
        this.spawnChance = spawnChance;
        this.hardLevel = hardLevel;
    }
}


