package Arena;

import Utils.ChatUtil;
import com.mimikcraft.mcc.Main;
import io.lumine.mythic.bukkit.utils.bossbar.BossBarColor;
import io.lumine.mythic.bukkit.utils.bossbar.BossBarStyle;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static Utils.WorldUtil.copyWorld;

public class Arena {

    private final String name;

    private final int minPlayers = 1;

    private final int maxPlayers = 8;

    private final int timeToStart = 60;

    private World arenaWorld;

    private final Map<Player, Location> onJoinLocation = new HashMap<>();

    private final Map<Player, Float> playerExp = new HashMap<>();

    private final Map<Player, Integer> playerLvl = new HashMap<>();

    private ArenaStages arenaStage = ArenaStages.CLOSED;

    private Game game;
    private ArenaLocation location;

    private final Map<Player, Map<ArtifactsTypes, Integer>> players = new HashMap<>();
    private final List<Player> ghosts = new ArrayList<>();
    private final List<Player> leavedPlayers = new ArrayList<>();

    public Arena(String name) {
        if (Bukkit.getWorld(name) == null){
            World source = Bukkit.getWorld("zombie");
            File sourceFolder = source.getWorldFolder();

            File file = new File("/home/container/"+name);

            copyWorld(sourceFolder, file);

            WorldCreator wc = new WorldCreator(name).generator(new EmptyChunkGenerator());
            wc.createWorld();
        }
        arenaWorld = Bukkit.getWorld(name);

        this.name = name;

        game = new Game(this);

    }

    public void reset(){
        arenaStage = ArenaStages.RESET;
        for (Player player : players.keySet()){
            leave(player);
        }
        Bukkit.unloadWorld(Bukkit.getWorld(name), true);;
        World source = Bukkit.getWorld("zombie");
        File sourceFolder = source.getWorldFolder();
        File file = new File("/home/container/"+name);
        copyWorld(sourceFolder, file);
        WorldCreator wc = new WorldCreator(name).generator(new EmptyChunkGenerator());
        wc.createWorld();
        game = new Game(this);
        players.clear();
        arenaStage = ArenaStages.CLOSED;
    }

    public void join(Player player){
        if (ArenaList.get(player) != null){
            ChatUtil.sendMessage(player, "Вы уже на арене!");
            return;
        }
        if (arenaStage == ArenaStages.CLOSED || arenaStage == ArenaStages.RESET || arenaStage == ArenaStages.GAME_ENDED) {
            ChatUtil.sendMessage(player, "Арена закрыта!");
            return;
        }
        if (players.size() >= maxPlayers){
            ChatUtil.sendMessage(player, "Арена заполнена!");
            return;
        }
        if(leavedPlayers.contains(player)){
            ChatUtil.sendMessage(player, "Вы уже сбежали с этой игры! Вернуться нельзя.");
            return;
        }
        playerExp.put(player,player.getExp());
        playerLvl.put(player,player.getLevel());
        player.setExp(0.0f);
        onJoinLocation.put(player, player.getLocation());
        sendArenaMessage(player.getDisplayName() + " присоединился!");
        player.getInventory().clear();
        player.getActivePotionEffects().clear();
        if (arenaStage == ArenaStages.WAITING){
            player.teleport(new Location(Bukkit.getWorld(name), location.getLobbyLocation().getX(),location.getLobbyLocation().getY(), location.getLobbyLocation().getZ()));
        } else{
            player.teleport(new Location(Bukkit.getWorld(name), location.getSpawnLocation().getX(),location.getSpawnLocation().getY(), location.getSpawnLocation().getZ()));
        }
        if (arenaStage == ArenaStages.CUTSCENE){
            player.setGameMode(GameMode.SPECTATOR);
            TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
            TabAPI.getInstance().getScoreboardManager().toggleScoreboard(tabPlayer, false);
            for (Player otherPlayer : getPlayers().keySet()) {
                otherPlayer.hidePlayer(Main.getInstance(), player);
                player.hidePlayer(Main.getInstance(), otherPlayer);
            }
        }
        if (players.size() == 1 && arenaStage != ArenaStages.CLOSED) {
            startGame();
        }
        players.put(player, new HashMap<>());

    }

    private void startGame(){

        arenaStage = ArenaStages.STARTING;

        new BukkitRunnable() {

            int ctr = timeToStart;

            @Override
            public void run() {
                if (ctr <= 0) {
                    arenaStage = ArenaStages.IN_PROCESS;
                    game.start();
                    cancel();
                }
                expTimerArena(ctr);

                if (ctr == 60 || ctr == 30 || ctr == 15 || ctr == 10 || ctr == 5 || ctr == 4 || ctr == 3 || ctr == 2 || ctr == 1) {
                    sendArenaTitle("&eДо старта игры осталось", "&c&l" + ctr);
                    sendArenaMessage("&eДо старта осталось &c " + ctr + " &eсекунд...");
                    expSoundArena();
                }
                ctr--;
                if (players.isEmpty()){
                    arenaStage = ArenaStages.WAITING;
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L,20L);
    }

    public void leave(Player player){
        if (arenaStage == ArenaStages.WAITING){
            player.setExp(playerExp.get(player));
            player.setLevel(playerLvl.get(player));
        }
        players.remove(player);
        if(arenaStage == ArenaStages.IN_PROCESS){
            leavedPlayers.add(player);
            sendArenaMessage(player.getDisplayName() + " сбежал!");
        }else{
            sendArenaMessage(player.getDisplayName() + " отключился!");
        }
        if (players.isEmpty() && arenaStage == ArenaStages.STARTING){
            reset();
        }
        player.teleport(onJoinLocation.get(player));
        game.removeBossBar(player);
        player.getInventory().clear();
        player.getActivePotionEffects().clear();
    }

    public void playerDie(Player player){
        ghosts.add(player);
        player.setGameMode(GameMode.SPECTATOR);
        new BukkitRunnable(){
            @Override
            public void run(){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give @a[distance=0..3] instant_health");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give @e[distance=0..3, tag=zombie] slowness");
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
        if (ghosts.size() >= players.size()){
            game.endGameBad();
        }
    }


    public void setLocationType(ArenaLocation.LocationTypes locationType){
        location = new ArenaLocation(locationType, arenaWorld);
    }

    public void sendArenaMessage(String message){
        for (Player player : players.keySet()){
            ChatUtil.sendMessage(player, message);
        }
    }

    public void sendArenaTitle (String message, String subMessage){
        for (Player player : players.keySet()){
            player.sendTitle(ChatColor.translateAlternateColorCodes('&', message), ChatColor.translateAlternateColorCodes('&', subMessage));
        }
    }

    public void expTimerArena (int value){
        for (Player player : players.keySet()){
            player.setLevel(value);
        }
    }
    public void expSoundArena (){
        for (Player player : players.keySet()){
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1,1);
        }
    }

    public void setPlayerExp(){
        for (Player player : players.keySet()){
            if(playerExp.get(player) == 0 || playerLvl.get(player) == 0){
                return;
            }
            player.setExp(playerExp.get(player));
            player.setLevel(playerLvl.get(player));
        }
    }

    public boolean canJoin(Player player) {

        switch(getLocation().getLocationType()){
            case HOSPITAL:
                return true;
            case MALL:
                return player.hasPermission("loc1.1");
            case GARAGE:
                return player.hasPermission("loc2.1");
            case FACTORY:
                return player.hasPermission("loc3.1");
            case METRO:
                return player.hasPermission("loc4.1");
        }

        ChatUtil.sendMessage(player, "&cНе удалось определить локацию");
        return true;
    }

    public void spawnRandomArtifact(Location location){
        File folder = new File(Main.getInstance().getDataFolder().getAbsolutePath());

        File file = new File(folder.getAbsolutePath() + "/Artifacts.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection artifactsSection = config.getConfigurationSection("Artifacts");
        Map<String, Double> artifacts = new HashMap<>();
        Set<String> artifactsNames = artifactsSection.getKeys(false);
        for(String artifact : artifactsNames){
            for(int i = 0; i < artifactsSection.getDoubleList(artifact).size(); i++){
                artifacts.put(artifact + "_" + (i + 1), artifactsSection.getDoubleList(artifact).get(i));
            }
        }
        int random = (int)(Math.random() * 10000);
        int temp = 0;
        String artifactName = "";
        for(Map.Entry<String, Double> entry : artifacts.entrySet()){
            temp += (int) (entry.getValue() * 100);
            if(random <= temp){
                artifactName = entry.getKey();
                break;
            }
        }
        getGame().spawnMythicEntity(location, artifactName);
    }


    public String getName() {
        return name;
    }


    public Map<Player, Map<ArtifactsTypes, Integer>> getPlayers() {
        return players;
    }


    public int getMinPlayers() {
        return minPlayers;
    }


    public Map<Player, Location> getOnJoinLocation() {
        return onJoinLocation;
    }

    public World getArenaWorld(){
        return arenaWorld;
    }

    public ArenaLocation getLocation(){
        return location;
    }

    public Game getGame(){
        return game;
    }

    public ArenaStages getArenaStage() {
        return arenaStage;
    }

    public void setArenaStage(ArenaStages arenaStage) {
        this.arenaStage = arenaStage;
    }
}