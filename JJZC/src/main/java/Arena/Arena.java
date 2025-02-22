package Arena;

import Utils.ChatUtil;
import com.mimikcraft.mcc.Main;
import io.lumine.mythic.bukkit.utils.bossbar.BossBarColor;
import io.lumine.mythic.bukkit.utils.bossbar.BossBarStyle;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Utils.WorldUtil.copyWorld;

public class Arena {

    private final String name;

    private final int minPlayers = 1;

    private final int maxPlayers = 8;

    private final int timeToStart = 10;

    private World arenaWorld;

    private final Map<Player, Location> onJoinLocation = new HashMap<>();

    private final Map<Player, Float> playerExp = new HashMap<>();

    private final Map<Player, Integer> playerLvl = new HashMap<>();

    private ArenaStages arenaStage = ArenaStages.CLOSED;

    private final Game game;
    private ArenaLocation location;

    private final List<Player> players = new ArrayList<>();
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
        for (Player player : players){
            leave(player);
        }
        Bukkit.unloadWorld(Bukkit.getWorld(name), true);;
        World source = Bukkit.getWorld("zombie");
        File sourceFolder = source.getWorldFolder();
        File file = new File("/home/container/"+name);
        copyWorld(sourceFolder, file);
        WorldCreator wc = new WorldCreator(name).generator(new EmptyChunkGenerator());
        wc.createWorld();
        arenaStage = ArenaStages.CLOSED;
    }

    public void join(Player player){
        if (ArenaList.get(player) != null){
            ChatUtil.sendMessage(player, "Вы уже на арене!");
            return;
        }
        if (arenaStage == ArenaStages.CLOSED) {
            ChatUtil.sendMessage(player, "Арена закрыта!");
            return;
        }
        if (players.size() == maxPlayers){
            ChatUtil.sendMessage(player, "Арена заполнена!");
            return;
        }
        if(leavedPlayers.contains(player)){
            ChatUtil.sendMessage(player, "Вы уже сбежали с этой игры! Вернуться нельзя.");
            return;
        }
        players.add(player);
        playerExp.put(player,player.getExp());
        playerLvl.put(player,player.getLevel());
        player.setExp(0.0f);
        onJoinLocation.put(player, player.getLocation());
        player.teleport(new Location(Bukkit.getWorld(name), location.getLobbyLocation().getX(),location.getLobbyLocation().getY(), location.getLobbyLocation().getZ()));
        sendArenaMessage(player.getDisplayName() + " присоединился!");
        if (players.size() == 1 && arenaStage != ArenaStages.CLOSED) {
            startGame();
        }

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
            sendArenaMessage(player.getDisplayName() + "сбежал!");
        }else{
            sendArenaMessage(player.getDisplayName() + " отключился!");
        }
        player.teleport(onJoinLocation.get(player));
        if (players.isEmpty() && arenaStage == ArenaStages.STARTING){
            reset();
        }
    }

    public void setLocationType(ArenaLocation.LocationTypes locationType){
        location = new ArenaLocation(locationType, arenaWorld);
    }

    public void sendArenaMessage(String message){
        for (Player player : players){
            ChatUtil.sendMessage(player, message);
        }
    }

    public void sendArenaTitle (String message, String subMessage){
        for (Player player : players){
            player.sendTitle(ChatColor.translateAlternateColorCodes('&', message), ChatColor.translateAlternateColorCodes('&', subMessage), 10, 40, 10);
        }
    }

    public void expTimerArena (int value){
        for (Player player : players){
            player.setLevel(value);
        }
    }
    public void expSoundArena (){
        for (Player player : players){
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1,1);
        }
    }

    public void setPlayerExp(){
        for (Player player : players){
            if(playerExp.get(player) == 0 || playerLvl.get(player) == 0){
                return;
            }
            player.setExp(playerExp.get(player));
            player.setLevel(playerLvl.get(player));
        }
    }

    public boolean canJoin(Player player) {

        if (getLocation().getLocationType() == ArenaLocation.LocationTypes.HOSPITAL) {
            return true;
        } else if (getLocation().getLocationType() == ArenaLocation.LocationTypes.MALL) {
            if (player.hasPermission("loc1.1")) {return true;}
            else {return false;}
        } else if (getLocation().getLocationType() == ArenaLocation.LocationTypes.GARAGE) {
            if (player.hasPermission("loc2.1")) {return true;}
            else {return false;}
        } else if (getLocation().getLocationType() == ArenaLocation.LocationTypes.FACTORY) {
            if (player.hasPermission("loc3.1")) {return true;}
            else {return false;}
        } else if (getLocation().getLocationType() == ArenaLocation.LocationTypes.METRO) {
            if (player.hasPermission("loc4.1")) {return true;}
            else {return false;}
        }
        ChatUtil.sendMessage(player, "&cНе удалось определить локацию");
        return true;
    }


    public String getName() {
        return name;
    }


    public List<Player> getPlayers() {
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