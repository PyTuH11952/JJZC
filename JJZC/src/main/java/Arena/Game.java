package Arena;

import Utils.ChatUtil;
import Utils.SmoothTeleportUtil;
import com.mimikcraft.mcc.Main;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.mimikcraft.mcc.ExecutableApi.giveExecutableItem;

public class Game {

    private final Arena arena;

    private int hardLevel = 1;

    private int wave = 0;

    private int wavesCount;

    private int infinityWave = 0;

    private boolean isInfinity = false;

    private int stage = 0;

    private int zombiesCount = 0;

    private int lifesCount = 3;

    private int addZombie;

    private double bossbarProgress;

    private int spawnersCount;

    public final List<Entity> mobs = new ArrayList<>();

    public int aliveZombies = 0;

    private int gameProgress = 0;

    private int glowingTime = 60;

    private boolean glowingTimer = false;

    private double maxWave = 0;

    private Map<Player, Integer> playerKills = new HashMap<>();


    BossBar bossbar = Bukkit.getServer().createBossBar("Осталось зомби: 0", BarColor.BLUE, BarStyle.SOLID);

    public Game(Arena arena) {
        this.arena = arena;
    }



    public void start() {
        CutScene cutScene = arena.getLocation().getCutScene();
        showCutScene(cutScene.locTitle,
                cutScene.floorsTitle,
                cutScene.doorsTitle,
                cutScene.locShowLocation,
                cutScene.floorsLocations,
                cutScene.doorsLocationcs);
    }

    private void getkit(Player player) {

        if (player.hasPermission("default")) {

            giveExecutableItem(player, "case1", 3);

        } else if (player.hasPermission("vip")) {
            giveExecutableItem(player, "case1", 10);
            giveExecutableItem(player, "bomba1", 3);
            giveExecutableItem(player, "bomba2", 2);
        } else if (player.hasPermission("vip+")) {
            giveExecutableItem(player, "case1", 10);
            giveExecutableItem(player, "case2", 3);
            giveExecutableItem(player, "bomba1", 5);
            giveExecutableItem(player, "bomba2", 3);

        } else if (player.hasPermission("premium")) {
            giveExecutableItem(player, "case1", 15);
            giveExecutableItem(player, "case2", 5);
            giveExecutableItem(player, "case3", 3);
            giveExecutableItem(player, "bomba1", 7);
            giveExecutableItem(player, "bomba2", 5);
            giveExecutableItem(player, "bomba3", 3);

        } else if (player.hasPermission("sponsor")) {
            giveExecutableItem(player, "case1", 16);
            giveExecutableItem(player, "case2", 6);
            giveExecutableItem(player, "case3", 4);
            giveExecutableItem(player, "bomba1", 8);
            giveExecutableItem(player, "bomba2", 6);
            giveExecutableItem(player, "bomba3", 4);

        } else if (player.hasPermission("elite")) {
            giveExecutableItem(player, "case1", 16);
            giveExecutableItem(player, "case2", 5);
            giveExecutableItem(player, "case3", 3);
            giveExecutableItem(player, "case4", 2);
            giveExecutableItem(player, "bomba1", 8);
            giveExecutableItem(player, "bomba2", 6);
            giveExecutableItem(player, "bomba3", 4);
            giveExecutableItem(player, "lom", 1);

        } else if (player.hasPermission("god")) {
            giveExecutableItem(player, "case1", 16);
            giveExecutableItem(player, "case2", 5);
            giveExecutableItem(player, "case3", 3);
            giveExecutableItem(player, "case4", 2);
            giveExecutableItem(player, "bomba1", 8);
            giveExecutableItem(player, "bomba2", 6);
            giveExecutableItem(player, "bomba3", 4);
            giveExecutableItem(player, "bomba5", 2);
            giveExecutableItem(player, "lom2", 1);

        }


    }

    private void preparePlayers() {
        for (Player player : arena.getPlayers().keySet()) {
            player.getInventory().clear();
            getkit(player);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            arena.setPlayerExp();
            Location location = arena.getLocation().getSpawnLocation();
            player.teleport(new Location(Bukkit.getWorld(arena.getArenaWorld().getName()), location.getX(), location.getY(), location.getZ()));
            ChatUtil.sendMessage(player, "&cБейся!");
            player.setGameMode(GameMode.ADVENTURE);
            TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
            TabAPI.getInstance().getScoreboardManager().toggleScoreboard(tabPlayer, true);
        }
        addZombie = arena.getLocation().getAddZombie();
        startNewWave();
    }

    private void showCutScene(String titleLoc, String titleStages, String titleDoors, Location showLoc, List<Location> showStages, List<Location> showDoors) {
        for (Player player : arena.getPlayers().keySet()) {
            player.setGameMode(GameMode.SPECTATOR);
            TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
            TabAPI.getInstance().getScoreboardManager().toggleScoreboard(tabPlayer, false);
            for (Player otherPlayer : arena.getPlayers().keySet()) {
                    otherPlayer.hidePlayer(Main.getInstance(), player);
                    player.hidePlayer(Main.getInstance(), otherPlayer);

            }
            player.getInventory().clear();
            player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1, 1);
        }
        showLoc(titleLoc, titleStages, titleDoors, showLoc,showStages,showDoors);
    }

    private void showLoc(String titleLoc, String titleStages, String titleDoors, Location showLoc, List<Location> showStages, List<Location> showDoors){
        new BukkitRunnable(){
            boolean bob = false;
            @Override
            public void run(){
                if(!bob){
                    for(Player player : arena.getPlayers().keySet()){
                        player.teleport(showLoc);
                    }
                    arena.sendArenaTitle(titleLoc, "");
                    bob = true;
                }else{
                    arena.sendArenaTitle(titleStages, "");
                    showStages(showStages, titleDoors, showDoors);
                    cancel();
                }
            }

        }.runTaskTimer(Main.getInstance(), 0L, 80L);
    }
    private void showStages(List<Location> showStages, String titleDoors, List<Location> showDoors){
        new BukkitRunnable(){
            int i = 0;
            @Override
            public void run(){
                if(i < showStages.size()){
                    for(Player player : arena.getPlayers().keySet()){
                        player.teleport(showStages.get(i));
                        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK,1,1);
                    }
                    i++;
                }else{
                    arena.sendArenaTitle(titleDoors, "");
                    showDoors(showDoors);

                    cancel();
                }
            }

        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }
    private void showDoors(List<Location> showDoors){
        new BukkitRunnable(){
            int i = 0;
            @Override
            public void run(){
                if(i < showDoors.size()){
                    for(Player player : arena.getPlayers().keySet()){
                        player.teleport(showDoors.get(i));
                        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                    }
                    i++;
                }else{
                    for (Player player : arena.getPlayers().keySet()){
                        player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT,1 ,1);
                    }
                    smoothTeleport(new ArrayList<>(arena.getPlayers().keySet()).get(0).getLocation(), arena.getLocation().getSpawnLocation());
                    cancel();
                }
            }

        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }
    private void smoothTeleport(Location loc1, Location loc2) {
            new BukkitRunnable() {
                double x1 = loc1.getX();
                double y1 = loc1.getY();
                double z1 = loc1.getZ();
                double x2 = loc2.getX();
                double y2 = loc2.getY();
                double z2 = loc2.getZ();
                double x;
                double y;
                double z;
                int i = 0;
                World world = loc1.getWorld();
                @Override
                public void run() {
                    double t = 0 + 0.01 * i;
                    i++;
                    x = (1-t) * x1 + t * x2;
                    y = (1-t) * y1 + t * y2;
                    z = (1-t) * z1 + t * z2;
                    if (t > 1){
                        for (Player player : arena.getPlayers().keySet()) {
                            for (Player otherPlayer : arena.getPlayers().keySet()) {
                                otherPlayer.showPlayer(Main.getInstance(), player);
                                player.showPlayer(Main.getInstance(), otherPlayer);
                            }
                        }
                        preparePlayers();
                        cancel();
                    }
                    else
                        for (Player player : arena.getPlayers().keySet()){
                            SmoothTeleportUtil.teleport(player, new Location(world, x, y, z));
                        }
                }
            }.runTaskTimer(Main.getInstance(), 0L, 1L);
    }
    public void spawnMob(Location location, String name){
        arena.getArenaWorld().spawnParticle(Particle.FLAME, location,10, 0.3, 0.3, 0.3, 0);
        arena.getArenaWorld().playSound(location, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);
        new BukkitRunnable(){
            @Override
            public void run(){
                 ActiveMob mythicEntity = MythicBukkit.inst().getMobManager().spawnMob(name, location, hardLevel);
                 Entity entity = mythicEntity.getEntity().getBukkitEntity();
                 cancel();
                }
        }.runTaskLater(Main.getInstance(),20L);
    }

    public void spawnMythicEntity(Location location, String name){
                ActiveMob mythicEntity = MythicBukkit.inst().getMobManager().spawnMob(name, location, hardLevel);
    }

    public void glowing(){
        glowingTime = 60;
        glowingTimer = true;
        new BukkitRunnable(){
            @Override
            public void run(){
                if (!glowingTimer){
                    cancel();
                }
                if (glowingTime == 0){
                    for (Entity entity : mobs) {
                        entity.setGlowing(true);
                    }
                    glowingTimer = false;
                    cancel();
                } else{
                    glowingTime--;
                }

            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
    private void addItems(Location location, ItemStack item) {
        Block block = location.getBlock();
        Inventory inv;
        Random random = new Random();
        if (block.getType() == Material.CHEST){
            Chest chest = (Chest) block.getState();
            inv = chest.getInventory();
            inv.setItem(random.nextInt(26), item);
        } else if (block.getType() == Material.BARREL){
            Barrel barrel = (Barrel) block.getState();
            inv = barrel.getInventory();
            inv.setItem(random.nextInt(26), item);
        }

    }

    private ItemStack getItem(String itemid, int count) {
        ItemStack item = null;
        Optional<ExecutableItemInterface> eiOpt = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(itemid);
        if (eiOpt.isPresent()) {
            item = eiOpt.get().buildItem(count, Optional.empty());
        }
        return item;
    }

    private void fillСhest(Location location, int locationNumber){
        Random random = new Random();
        int armorWeaponCount = random.nextInt(3);
        int materialCount = random.nextInt(3);
        int differentCount = random.nextInt(2);
        int locItemCount = random.nextInt(2);

        Block block = location.getBlock();
        Inventory inv;
        if (block.getType() == Material.CHEST){
            Chest chest = (Chest) block.getState();
            inv = chest.getInventory();
            inv.clear();
        } else if (block.getType() == Material.BARREL){
            Barrel barrel = (Barrel) block.getState();
            inv = barrel.getInventory();
            inv.clear();
        }

        for (int i = 0; i <= armorWeaponCount; i++) {
            double d = Math.random();

            if (d < 0.39) {
                int tir1armor = random.nextInt(11) + 1;
                addItems(location, getItem("tir1armor" + tir1armor, 1));
            } else if (d < 0.78) {
                int tir1armor = random.nextInt(9) + 1;
                addItems(location, getItem("tir1weapon" + tir1armor, 1));
            } else if (d < 0.83) {
                int tir2armor = random.nextInt(7) + 1;
                addItems(location, getItem("tir2armor" + tir2armor, 1));
            } else if (d < 0.88) {
                int tir2weapon = random.nextInt(3) + 1;
                addItems(location, getItem("tir2weapon" + tir2weapon, 1));
            } else if (d < 0.91) {
                int tir3armor = random.nextInt(7) + 1;
                addItems(location, getItem("tir3armor" + tir3armor, 1));
            } else if (d < 0.94) {
                int tir3weapon = random.nextInt(2) + 1;
                addItems(location, getItem("tir3weapon" + tir3weapon, 1));
            } else if (d < 0.96) {
                int tir4armor = random.nextInt(7) + 1;
                addItems(location, getItem("tir4armor" + tir4armor, 1));
            } else if (d < 0.98) {
                int tir4weapon = random.nextInt(2) + 1;
                addItems(location, getItem("tir4weapon" + tir4weapon, 1));
            } else if (d < 0.99) {
                int tir5armor = random.nextInt(7) + 1;
                addItems(location, getItem("tir5armor" + tir5armor, 1));
            } else {
                int tir5weapon = random.nextInt(2) + 1;
                addItems(location, getItem("tir5weapon" + tir5weapon, 1));
            }
        }
        for (int i = 0; i <= materialCount; i++) {
            double d = Math.random();
            int matcount = random.nextInt(2) + 1;
            if (d < 0.5) {
                addItems(location, getItem("material1", matcount));
            } else if (d < 0.75) {
                addItems(location, getItem("material2", matcount));
            } else if (d < 0.9) {
                addItems(location, getItem("material3", matcount));
            } else if (d < 0.97) {
                addItems(location, getItem("material4", matcount));
            } else {
                addItems(location, getItem("material5", matcount));
            }
        }
        for (int i = 0; i <= differentCount; i++){
            double d = Math.random();
            int diffcount = random.nextInt(2) + 1;
            if (d < 0.7){
                int eda = random.nextInt(4) + 1;
                addItems(location, getItem("eda" + eda, diffcount));
            } else if (d < 0.77){
                int bomba = random.nextInt(4) + 1;
                addItems(location, getItem("bomba" + bomba, 1));
            } else if (d < 0.84){
                addItems(location, getItem("repair", 1));
            } else if (d < 0.91){
                addItems(location, getItem("hpregen", 1));
            } else if (d < 0.97){
                addItems(location, getItem("dopitem2", 1));
            } else if (d < 0.98){
                addItems(location, getItem("dopitem1", 1));
            } else if (d < 0.99){
                addItems(location, getItem("lom", 1));
            } else {
                addItems(location, getItem("lom2", 1));
            }
        }
        for (int i = 0; i <= locItemCount; i++){
            double d = Math.random();
            int loccount = random.nextInt(2) + 1;
            int lootcount = random.nextInt(3) + 1;
            if (d < 0.02){
                addItems(location, getItem("loc"+locationNumber+"loot"+lootcount, loccount));
            }


        }
    }

    public void sendBossBar() {
        for (Player player : arena.getPlayers().keySet()) {
            bossbar.addPlayer(player);
        }
        if (wave == 1){
            bossbarProgress = (double)(mobs.size()-addZombie)/(double)(zombiesCount-addZombie);
        } else{
            bossbarProgress = (double)(mobs.size()-addZombie)/(double)zombiesCount;
        }
        if (bossbarProgress <= 0.0) {
            bossbar.removeAll();
            return;
        }
        bossbar.setTitle("Осталось зомби: " + (aliveZombies - addZombie));
        bossbar.setProgress(bossbarProgress);
    }
    public void startNewWave(){
        glowingTimer = false;
        if (wave == 0){
            for (int i = 0; i < arena.getLocation().getStages().size(); i++){
                maxWave += arena.getLocation().getStages().get(i).wavesCount;
            }
        }
        if (wavesCount == (wave+1) && stage == arena.getLocation().getStages().size()){
            arena.sendArenaTitle("Босс!", "");
            clearMobs();
            spawnMythicEntity(arena.getLocation().getBossLocation(), arena.getLocation().getBossName());
            wave++;
            return;
        }
        if(wavesCount == wave){
            stage++;
            wavesCount = wavesCount + arena.getLocation().getStages().get(stage - 1).wavesCount;
        }
        wave++;

        gameProgress = (int)((double)wave/maxWave*(double)100);
        zombiesCount = (int)(wave*arena.getPlayers().size()*arena.getLocation().getLocationFactor()+addZombie+1);
        if(wave == wavesCount){
            for(Map.Entry<Location, Material> entry : arena.getLocation().getStages().get(stage - 1).structureChanges.entrySet()){
                arena.getArenaWorld().getBlockAt(entry.getKey()).setType(entry.getValue());
            }
            zombiesCount *= 2;
        }
        aliveZombies += zombiesCount;
        if (wave == wavesCount){
            arena.sendArenaTitle("&cВолна: " + wave, "&cКол-во зомби: " + zombiesCount);
        } else {
            arena.sendArenaTitle("Волна: " + wave, "Кол-во зомби: " + zombiesCount);
        }
        new BukkitRunnable(){
            int spawnedZombies = 0;
            @Override
            public void run() {
                if(spawnedZombies == (zombiesCount-1)){
                    cancel();
                }
                spawnedZombies++;
                spawnersCount = arena.getLocation().getStages().get(stage - 1).spawners.size();

                String zombieName = "";
                List<Zombie> tempZombies = arena.getLocation().getZombies();
                double extraChances = 0;
                int firstHardLevelZombiesCount = 0;
                for(Zombie zombie : tempZombies){
                    if(zombie.hardLevel > hardLevel){
                        tempZombies.remove(zombie);
                    }
                    if(zombie.hardLevel > 1){
                        extraChances += zombie.spawnChance;
                    }else{
                        firstHardLevelZombiesCount++;
                    }
                }
                extraChances /= firstHardLevelZombiesCount;
                for(Zombie zombie: tempZombies){
                    if(extraChances == 0){
                        break;
                    }
                    if(zombie.hardLevel == 1){
                        tempZombies.get(tempZombies.indexOf(zombie)).spawnChance -= extraChances;
                    }
                }
                int random = (int)(Math.random() * 10000);
                int temp = 0;
                for(Zombie zombie : tempZombies){
                    temp += (int) (zombie.spawnChance * 100);
                    if(random <= temp){
                        zombieName = zombie.name;
                        break;
                    }
                }
                spawnMob(arena.getLocation().getStages().get(stage - 1).spawners.get((int)(Math.random() * spawnersCount)), zombieName);
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    public void dopWave(){
        int randomMiniGame = (int)(1 + Math.random() * 4);
        spawnersCount = arena.getLocation().getStages().get(stage - 1).spawners.size();
        clearMobs();
        switch (randomMiniGame){
            case 1:
                arena.sendArenaTitle("Дополнительная волна!","Минибосс");
                int randomValue = (int) (1 + Math.random() * 3);
                spawnMob(arena.getLocation().getBossLocation(), "miniboss"+randomValue);
                break;
            case 2:
                arena.sendArenaTitle("Дополнительная волна!","Пеньята");
                spawnMob(arena.getLocation().getBossLocation(), "peniata");
                break;
            case 3:
                arena.sendArenaTitle("Дополнительная волна!","Загрязнение");
                addZombie *= 3;
                zombiesCount = (int)(wave*arena.getPlayers().size()*arena.getLocation().getLocationFactor()*10+addZombie+1);
                aliveZombies += zombiesCount/10;
                new BukkitRunnable(){
                    int spawnedZombies = 0;
                    @Override
                    public void run(){
                        if(spawnedZombies == (zombiesCount/10-1)){
                            cancel();
                        }
                        spawnedZombies++;
                        spawnMob(arena.getLocation().getStages().get(stage - 1).spawners.get((int)(Math.random() * spawnersCount)), "kaka");
                    }
                }.runTaskTimer(Main.getInstance(), 0, 20);
                break;
            case 4:
                arena.sendArenaTitle("Дополнительная волна!","Тротиловые зомби");
                addZombie = 0;
                zombiesCount = (int)(wave*arena.getPlayers().size()*arena.getLocation().getLocationFactor()/2+addZombie+1);
                aliveZombies += zombiesCount;
                new BukkitRunnable(){
                    int spawnedZombies = 0;
                    @Override
                    public void run(){
                        if(spawnedZombies == (zombiesCount-1)){
                            cancel();
                        }
                        spawnedZombies++;
                        spawnMob(arena.getLocation().getStages().get(stage - 1).spawners.get((int)(Math.random() * spawnersCount)), "bombzombie");
                    }
                }.runTaskTimer(Main.getInstance(), 0, 20);
                break;
        }

    }

    public void endGame() {
        arena.setArenaStage(ArenaStages.GAME_ENDED);
        for (Entity entity : mobs){
            entity.remove();
        }
        arena.sendArenaTitle("&aПобеда!", "");
        for (Player player : arena.getPlayers().keySet()){
            ChatUtil.sendMessage(player, "&c&lАрена перезагрузиться через 10 секунд...");
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                arena.reset();
                cancel();
            }
        }.runTaskLater(Main.getInstance(), 200);
    }

    public void clearMobs(){
        for (Entity entity : mobs){
            entity.remove();
        }
        mobs.clear();
        aliveZombies = 0;
    }

    public boolean isInfinity() {
        return isInfinity;
    }

    public void setAddZombie(int addZombie) {
        this.addZombie = addZombie;
    }

    public int getAddZombie() {
        return addZombie;
    }

    public Map<Player, Integer> getPlayerKills() {
        return playerKills;
    }

    public int getLifesCount() {
        return lifesCount;
    }

    public int getHardLevel() {
        return hardLevel;
    }

    public void setLifesCount(int lifesCount) {
        this.lifesCount = lifesCount;
    }

    public void setHardLevel(int hardLevel) {
        this.hardLevel = hardLevel;
    }

    public int getGameProgress() {
        return gameProgress;
    }

    public int getGlowingTime() {
        return glowingTime;
    }

    public boolean isGlowingTimer() {
        return glowingTimer;
    }

    public void setGlowingTimer(boolean glowingTimer) {
        this.glowingTimer = glowingTimer;
    }
}

