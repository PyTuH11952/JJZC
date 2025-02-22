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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

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

    private int life = 3;

    private int addZombie;

    private double bossbarProgress;

    public final List<Entity> mobs = new ArrayList<>();
    public int aliveZombies = 0;

    BossBar bossbar = Bukkit.getServer().createBossBar("Осталось зомби: " + (aliveZombies - 2), BarColor.BLUE, BarStyle.SOLID);


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
        for (Player player : arena.getPlayers()) {
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
        for (Player player : arena.getPlayers()) {
            player.setGameMode(GameMode.SPECTATOR);
            TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
            TabAPI.getInstance().getScoreboardManager().toggleScoreboard(tabPlayer, false);
            for (Player otherPlayer : arena.getPlayers()) {
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
                    for(Player player : arena.getPlayers()){
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
                    for(Player player : arena.getPlayers()){
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
                    for(Player player : arena.getPlayers()){
                        player.teleport(showDoors.get(i));
                        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                    }
                    i++;
                }else{
                    for (Player player : arena.getPlayers()){
                        player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT,1 ,1);
                    }
                    smoothTeleport(arena.getPlayers().get(0).getLocation(), arena.getLocation().getSpawnLocation());
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
                        for (Player player : arena.getPlayers()) {
                            for (Player otherPlayer : arena.getPlayers()) {
                                otherPlayer.showPlayer(Main.getInstance(), player);
                                player.showPlayer(Main.getInstance(), otherPlayer);
                            }
                        }
                        preparePlayers();
                        cancel();
                    }
                    else
                        for (Player player : arena.getPlayers()){
                            SmoothTeleportUtil.teleport(player, new Location(world, x, y, z));
                        }
                }
            }.runTaskTimer(Main.getInstance(), 0L, 1L);
    }
    private void spawnMob(Location location, String name){
        new BukkitRunnable(){
            boolean isParticlesSpawned = false;
            @Override
            public void run(){
                if(!isParticlesSpawned){
                    arena.getArenaWorld().spawnParticle(Particle.FLAME, location,10, 0.3, 0.3, 0.3, 0);
                    arena.getArenaWorld().playSound(location, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1, 1);
                    isParticlesSpawned = true;
                }else{
                    ActiveMob mythicentity = MythicBukkit.inst().getMobManager().spawnMob(name, location, hardLevel);
                    Entity entity = mythicentity.getEntity().getBukkitEntity();
                    mobs.add(entity);
                    cancel();
                }
            }

        }.runTaskTimer(Main.getInstance(), 0L, 20L);

    }
    private void glowing(){
        for (Entity entity : mobs) {
            entity.setGlowing(true);
        }
    }
    private void additems(Location location, ItemStack item) {
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

    private ItemStack getitem (String itemid, int count) {
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
                additems(location, getitem("tir1armor" + tir1armor, 1));
            } else if (d < 0.78) {
                int tir1armor = random.nextInt(9) + 1;
                additems(location, getitem("tir1weapon" + tir1armor, 1));
            } else if (d < 0.83) {
                int tir2armor = random.nextInt(7) + 1;
                additems(location, getitem("tir2armor" + tir2armor, 1));
            } else if (d < 0.88) {
                int tir2weapon = random.nextInt(3) + 1;
                additems(location, getitem("tir2weapon" + tir2weapon, 1));
            } else if (d < 0.91) {
                int tir3armor = random.nextInt(7) + 1;
                additems(location, getitem("tir3armor" + tir3armor, 1));
            } else if (d < 0.94) {
                int tir3weapon = random.nextInt(2) + 1;
                additems(location, getitem("tir3weapon" + tir3weapon, 1));
            } else if (d < 0.96) {
                int tir4armor = random.nextInt(7) + 1;
                additems(location, getitem("tir4armor" + tir4armor, 1));
            } else if (d < 0.98) {
                int tir4weapon = random.nextInt(2) + 1;
                additems(location, getitem("tir4weapon" + tir4weapon, 1));
            } else if (d < 0.99) {
                int tir5armor = random.nextInt(7) + 1;
                additems(location, getitem("tir5armor" + tir5armor, 1));
            } else {
                int tir5weapon = random.nextInt(2) + 1;
                additems(location, getitem("tir5weapon" + tir5weapon, 1));
            }
        }
        for (int i = 0; i <= materialCount; i++) {
            double d = Math.random();
            int matcount = random.nextInt(2) + 1;
            if (d < 0.5) {
                additems(location, getitem("material1", matcount));
            } else if (d < 0.75) {
                additems(location, getitem("material2", matcount));
            } else if (d < 0.9) {
                additems(location, getitem("material3", matcount));
            } else if (d < 0.97) {
                additems(location, getitem("material4", matcount));
            } else {
                additems(location, getitem("material5", matcount));
            }
        }
        for (int i = 0; i <= differentCount; i++){
            double d = Math.random();
            int diffcount = random.nextInt(2) + 1;
            if (d < 0.7){
                int eda = random.nextInt(4) + 1;
                additems(location, getitem("eda" + eda, diffcount));
            } else if (d < 0.77){
                int bomba = random.nextInt(4) + 1;
                additems(location, getitem("bomba" + bomba, 1));
            } else if (d < 0.84){
                additems(location, getitem("repair", 1));
            } else if (d < 0.91){
                additems(location, getitem("hpregen", 1));
            } else if (d < 0.97){
                additems(location, getitem("dopitem2", 1));
            } else if (d < 0.98){
                additems(location, getitem("dopitem1", 1));
            } else if (d < 0.99){
                additems(location, getitem("lom", 1));
            } else {
                additems(location, getitem("lom2", 1));
            }
        }
        for (int i = 0; i <= locItemCount; i++){
            double d = Math.random();
            int loccount = random.nextInt(2) + 1;
            int lootcount = random.nextInt(3) + 1;
            if (d < 0.02){
                additems(location, getitem("loc"+locationNumber+"loot"+lootcount, loccount));
            }


        }
    }

    public void sendBossBar() {
        for (Player player : arena.getPlayers()) {
            bossbar.addPlayer(player);
        }
        if (wave == 1){
            bossbarProgress = (double) aliveZombies / zombiesCount;
        } else{
            bossbarProgress = (double) (aliveZombies - addZombie) / zombiesCount;
        }
        if (bossbarProgress <= 0) {
            bossbar.removeAll();
            return;
        }
        bossbar.setTitle("Осталось зомби: " + (aliveZombies - addZombie));
        bossbar.setProgress(bossbarProgress);
    }
    public void startNewWave(){
        if(wavesCount == wave){
            stage++;
            if (stage == (arena.getLocation().getStages().size()+1)){
                arena.sendArenaTitle("&cВолна: " + wave, "Босс!");
                spawnMob(arena.getLocation().getBossLocation(), arena.getLocation().getBossName());
                return;
            }
            wavesCount = wavesCount + arena.getLocation().getStages().get(stage - 1).wavesCount;
        }
        wave++;

        zombiesCount = (int)(wave*arena.getPlayers().size()*arena.getLocation().getLocationFactor()+addZombie+1);
        if(wave == wavesCount){
            zombiesCount *= 2;
        }
        aliveZombies += zombiesCount;
        if (wave == wavesCount){
            arena.sendArenaTitle("&cВолна: " + wave, "&cКол-во зомби: " + zombiesCount);
        } else
        {arena.sendArenaTitle("Волна: " + wave, "Кол-во зомби: " + zombiesCount);}
        sendBossBar();
        new BukkitRunnable(){
            int spawnedZombies = 0;
            @Override
            public void run() {
                if(spawnedZombies == (zombiesCount-1)){
                    cancel();
                }
                spawnedZombies++;
                int spawnersCount = arena.getLocation().getStages().get(stage - 1).spawners.size();

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

    public void endGame() {
        arena.setArenaStage(ArenaStages.GAME_ENDED);
        for (Entity entity : mobs){
            entity.remove();
        }
        arena.sendArenaTitle("&aПобеда!", "");
        for (Player player : arena.getPlayers()){
            ChatUtil.sendMessage(player, "&c&lАрена перезагрузиться через 10 секунд...");
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                arena.reset();
            }
        }.runTaskLater(Main.getInstance(), 200);
    }

    public boolean isInfinity() {
        return isInfinity;
    }
}

