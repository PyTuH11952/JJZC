package Arena;

import Utils.StringToLocationUtil;
import com.mimikcraft.mcc.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ArenaLocation {

    private LocationTypes locationType;

    private String name;
    private Location spawnLocation;
    private Location lobbyLocation;
    private Location bossLocation;
    private World world;
    private CutScene cutScene;
    private double locationFactor;
    private int addZombie;
    private String bossName;
    private final List<Location> chests = new ArrayList<>();
    private final List<Zombie> zombies = new ArrayList<>();
    private final List<SpawnMob> mobsSpawn = new ArrayList<>();
    private final List<Stage> stages = new ArrayList<>();
    private final Map<Location, Location> doors = new HashMap<>();
    private final List<CustomBlock> customBlocks = new ArrayList<>();
    private Train train;

    public ArenaLocation(LocationTypes locationType, World world){
        this.world = world;
        if(locationType != null){
            setLocationType(locationType);
        }
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

        name = locationSection.getString("name");

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

        String[] bossCoordinatesStr = locationSection.getString("bossLocation").split(" ");
        double bossX = Double.parseDouble(bossCoordinatesStr[0]);
        double bossY = Double.parseDouble(bossCoordinatesStr[1]);
        double bossZ = Double.parseDouble(bossCoordinatesStr[2]);
        bossLocation = new Location(world, bossX, bossY, bossZ);

        locationFactor = Double.parseDouble(locationSection.getString("locationFactor"));

        addZombie = Integer.parseInt(locationSection.getString("addZombie"));

        bossName = (locationSection.getString("bossName"));

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
        for(String section : stagesKeys) {
            ConfigurationSection stageSection = stagesSection.getConfigurationSection(section);
            List<String> spawnersCoordinatesStr = stageSection.getStringList("spawners");
            List<Location> tempCordsList = new ArrayList<>();
            for (String spawnerCords : spawnersCoordinatesStr) {
                String[] spawnerCordsStr = spawnerCords.split(" ");
                double x = Double.parseDouble(spawnerCordsStr[0]);
                double y = Double.parseDouble(spawnerCordsStr[1]);
                double z = Double.parseDouble(spawnerCordsStr[2]);
                tempCordsList.add(new Location(world, x, y, z));
            }
            List<String> structureChangesStr = stageSection.getStringList("structureChanges");
            HashMap<Location, Material> structureChanges = new HashMap<>();
            for (String line : structureChangesStr) {
                String[] blockCordsStr = line.split(":")[0].split(" ");
                double x = Double.parseDouble(blockCordsStr[0]);
                double y = Double.parseDouble(blockCordsStr[1]);
                double z = Double.parseDouble(blockCordsStr[2]);
                structureChanges.put(new Location(world, x, y, z), Material.valueOf(line.split(":")[1].toUpperCase()));
            }
            int wavesCount = stageSection.getInt("wavesCount");
            List<String> commands = stageSection.getStringList("commands");

            ConfigurationSection mobsSection = stageSection.getConfigurationSection("mobsSpawn");
            if (mobsSection != null) {
                Set<String> mobsKeys = mobsSection.getKeys(false);
                for (String mobKey : mobsKeys) {
                    String mobName = mobsSection.getString(mobKey + ".name");
                    int loops = Integer.parseInt(mobsSection.getString(mobKey + ".loops"));
                    int count = Integer.parseInt(mobsSection.getString(mobKey + ".count"));
                    boolean randomZombie = Boolean.parseBoolean(mobsSection.getString(mobKey + ".randomZombie"));
                    String[] mobCordsStr = mobsSection.getString(mobKey + ".location").split(" ");
                    double mobX = Double.parseDouble(mobCordsStr[0]);
                    double mobY = Double.parseDouble(mobCordsStr[1]);
                    double mobZ = Double.parseDouble(mobCordsStr[2]);
                    Location mobLocation = new Location(world, mobX, mobY, mobZ);
                    SpawnMob mob = new SpawnMob(mobName, loops, count, mobLocation, randomZombie);
                    mobsSpawn.add(mob);
                }
            }
            List<String> collapseCoordinatesStr = stageSection.getStringList("collapse");
            List<Location> collapseCords = new ArrayList<>();
            for (String cords : collapseCoordinatesStr) {
                String[] collapseCordsStr = cords.split(" ");
                double x = Double.parseDouble(collapseCordsStr[0]);
                double y = Double.parseDouble(collapseCordsStr[1]);
                double z = Double.parseDouble(collapseCordsStr[2]);
                collapseCords.add(new Location(world, x, y, z));
            }
            if (stageSection.getConfigurationSection("train") != null) {
                ConfigurationSection trainSection = stageSection.getConfigurationSection("train");
                List<String> trainZoneStr = trainSection.getStringList("zone");
                List<Location> trainZoneCords = new ArrayList<>();
                for (String cords : trainZoneStr) {
                    String[] trainZone = cords.split(" ");
                    double x = Double.parseDouble(trainZone[0]);
                    double y = Double.parseDouble(trainZone[1]);
                    double z = Double.parseDouble(trainZone[2]);
                    trainZoneCords.add(new Location(world, x, y, z));
                }
                Location trainLocation = StringToLocationUtil.getLocation(world, trainSection.getString("trainLocation").split(" "));
                Location newTrainLocation = StringToLocationUtil.getLocation(world, trainSection.getString("newTrainLocation").split(" "));
                Location newSpawn = StringToLocationUtil.getLocation(world, trainSection.getString("newSpawn").split(" "));

                train = new Train(trainZoneCords, trainLocation, newTrainLocation, newSpawn);
            } else {
                train = null;
            }
            stages.add(new Stage(tempCordsList, structureChanges, wavesCount, commands, mobsSpawn, collapseCords, train));
        }

        ConfigurationSection cutSceneSection = locationSection.getConfigurationSection("cutScene");
        String locTitle = cutSceneSection.getString("locTitle");
        String floorsTitle = cutSceneSection.getString("floorsTitle");
        String doorsTitle = cutSceneSection.getString("doorsTitle");
        String[] showLocCordsStr = cutSceneSection.getString("locShowLocation").split(" ");
        Location locShowLocation = new Location(world, Double.parseDouble(showLocCordsStr[0]), Double.parseDouble(showLocCordsStr[1]), Double.parseDouble(showLocCordsStr[2]), Float.parseFloat(showLocCordsStr[3]), Float.parseFloat(showLocCordsStr[4]));
        List<String> floorsLocationsStr = cutSceneSection.getStringList("floorsLocations");
        List<Location> floorsLocations = new ArrayList<>();
        for(String floorsLocationStr : floorsLocationsStr){
            String[] floorsCordsStr = floorsLocationStr.split(" ");
            floorsLocations.add(new Location(world, Double.parseDouble(floorsCordsStr[0]), Double.parseDouble(floorsCordsStr[1]), Double.parseDouble(floorsCordsStr[2]), Float.parseFloat(floorsCordsStr[3]), Float.parseFloat(floorsCordsStr[4])));
        }
        List<String> doorsLocationsStr = cutSceneSection.getStringList("doorsLocations");
        List<Location> doorsLocations = new ArrayList<>();
        for(String doorsLocationStr : doorsLocationsStr){
            String[] doorsCordsStr = doorsLocationStr.split(" ");
            doorsLocations.add(new Location(world, Double.parseDouble(doorsCordsStr[0]), Double.parseDouble(doorsCordsStr[1]), Double.parseDouble(doorsCordsStr[2]), Float.parseFloat(doorsCordsStr[3]), Float.parseFloat(doorsCordsStr[4])));
        }
        cutScene = new CutScene(locTitle, floorsTitle, doorsTitle, locShowLocation, floorsLocations, doorsLocations);

        ConfigurationSection doorsSection = locationSection.getConfigurationSection("doors");
        Set<String> doorsKeys = doorsSection.getKeys(false);
        for(String section : doorsKeys){
            String[] doorCordsStr = doorsSection.getString(section + ".doorLocation").split(" ");
            double doorX = Double.parseDouble(doorCordsStr[0]);
            double doorY = Double.parseDouble(doorCordsStr[1]);
            double doorZ = Double.parseDouble(doorCordsStr[2]);
            Location doorLocation = new Location(world, doorX, doorY, doorZ);

            String[] artCordsStr = doorsSection.getString(section + ".artifactSpawnLocation").split(" ");
            double artX = Double.parseDouble(artCordsStr[0]);
            double artY = Double.parseDouble(artCordsStr[1]);
            double artZ = Double.parseDouble(artCordsStr[2]);
            Location artLocation = new Location(world, artX, artY, artZ);
            doors.put(doorLocation, artLocation);
        }
        ConfigurationSection customBlocksSection = locationSection.getConfigurationSection("customBlocks");
        Set<String> customBlocksCoordStr = customBlocksSection.getKeys(false);
        for(String customBlockCoordStr: customBlocksCoordStr){
            String[] customBlockCoordsStr = customBlockCoordStr.split(" ");
            double doorX = Double.parseDouble(customBlockCoordsStr[0]);
            double doorY = Double.parseDouble(customBlockCoordsStr[1]);
            double doorZ = Double.parseDouble(customBlockCoordsStr[2]);
            Location customBlockLocation = new Location(world, doorX, doorY, doorZ);
            if(customBlocksSection.getString(customBlockCoordStr).equals("anvil")){
                CustomAnvil customAnvil = new CustomAnvil(customBlockLocation, "anvil");
                customBlocks.add(customAnvil);
            }else{
                CustomBlock customBlock = new CustomBlock(customBlockLocation, customBlocksSection.getString(customBlockCoordStr));
                customBlocks.add(customBlock);
            }
        }

    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public Location getLobbyLocation(){
        return lobbyLocation;
    }

    public Location getBossLocation(){
        return bossLocation;
    }

    public List<Location> getChests() {
        return chests;
    }

    public Map<Location, Location> getDoors() {
        return doors;
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

    public int getAddZombie(){
        return addZombie;
    }

    public String getBossName(){
        return bossName;
    }

    public LocationTypes getLocationType() {
        return locationType;
    }

    public CutScene getCutScene() {
        return cutScene;
    }

    public List<CustomBlock> getCustomBlocks() {
        return customBlocks;
    }

    public String getName(){
        return name;
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
    HashMap<Location, Material> structureChanges;
    int wavesCount;
    List<String> commands;
    List<SpawnMob> mobsSpawn;
    List<Location> collapseCords;
    Train train;
    public Stage(List<Location> spawners, HashMap<Location, Material> structureChanges, int wavesCount, List<String> commands, List<SpawnMob> mobsSpawn, List<Location> collapseCords, Train train){
        this.spawners = spawners;
        this.structureChanges = structureChanges;
        this.wavesCount = wavesCount;
        this.commands = commands;
        this.mobsSpawn = mobsSpawn;
        this.collapseCords = collapseCords;
        this.train = train;

    }
    class WaveEvents{


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

class SpawnMob{
    String name;
    int loops;
    int count;
    Location location;
    boolean randomZombie;
    public SpawnMob(String name, int loops, int count, Location location, boolean randomZombie){
        this.name = name;
        this.loops = loops;
        this.count = count;
        this.location = location;
        this.randomZombie = randomZombie;
    }
}


class CutScene {
    String locTitle;
    String floorsTitle;
    String doorsTitle;
    Location locShowLocation;
    List<Location> floorsLocations;
    List<Location> doorsLocationcs;

    public CutScene(String locTitle, String floorsTitle, String doorsTitle, Location locShowLocation, List<Location> floorsLocations, List<Location> doorsLocationcs) {
        this.locTitle = locTitle;
        this.floorsTitle = floorsTitle;
        this.doorsTitle = doorsTitle;
        this.locShowLocation = locShowLocation;
        this.floorsLocations = floorsLocations;
        this.doorsLocationcs = doorsLocationcs;
    }
}

