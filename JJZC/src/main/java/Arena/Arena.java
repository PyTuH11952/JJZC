package Arena;

import Utils.ChatUtil;
import com.mimikcraft.mcc.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.lumine.mythic.bukkit.utils.Players.spawnParticle;

public class Arena {

    private final String name;

    private final int minPlayers = 1;

    private final int maxPlayers = 8;

    private final int timeToStart = 10;

    private boolean isInfinity = false;

    private World arenaWorld;

    private final Map<Player, Location> onJoinLocation = new HashMap<>();

    private final Map<Player, Float> playerExp = new HashMap<>();

    private final Map<Player, Integer> playerLvl = new HashMap<>();

    private ArenaStages arenaStage = ArenaStages.WAITING;

    private final Game game;
    private ArenaLocation location;

    private final List<Player> players = new ArrayList<>();
    private final List<Player> ghosts = new ArrayList<>();

    public Arena(String name) {
        if (Bukkit.getWorld(name) == null){
            WorldCreator worldCreator = new WorldCreator(name);
            worldCreator.environment(World.Environment.NORMAL);
            worldCreator.generator(new EmptyChunkGenerator());
            worldCreator.generateStructures(false);
            worldCreator.createWorld();
        }
        arenaWorld = Bukkit.getWorld(name);

        this.name = name;

        game = new Game(this);

    }

    public void reset(){
        arenaStage = ArenaStages.RESET;
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
        players.add(player);
        onJoinLocation.put(player, player.getLocation());
        player.teleport(location.getLobbyLocation());
        sendArenaMessage(player.getDisplayName() + " присоединился!");
        if (!players.isEmpty() && arenaStage != ArenaStages.CLOSED) {
            startGame();
        }

    }

    private void startGame(){

        arenaStage = ArenaStages.STARTING;
        saveExpArena();

        new BukkitRunnable() {

            int ctr = timeToStart;

            @Override
            public void run() {
                if (ctr <= 0) {
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
        sendArenaMessage(player.getDisplayName() + " отключился!");
        player.teleport(onJoinLocation.get(player));
        if (players.isEmpty() && arenaStage == ArenaStages.STARTING){
            arenaStage = ArenaStages.WAITING;
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

    public void saveExpArena(){
        for (Player player : players){
            playerExp.put(player,player.getExp());
            playerLvl.put(player,player.getLevel());
            player.setExp(0.0f);
        }
    }
    public void setPlayerExp(){
        for (Player player : players){
            player.setExp(playerExp.get(player));
            player.setLevel(playerLvl.get(player));
        }
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

    public void setInfinity(boolean infinity) {
        isInfinity = infinity;
    }
}