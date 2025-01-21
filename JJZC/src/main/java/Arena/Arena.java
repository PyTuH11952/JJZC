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

    private final boolean isInfinity = false;

    private final Map<Player, Location> onJoinLocation = new HashMap<>();

    private Map<Player, Float> playerExp = new HashMap<>();

    private Map<Player, Integer> playerExp2 = new HashMap<>();

    private Location lobby;

    private ArenaStage arenaStage = ArenaStage.WAITING;

    private final Game game;

    private final List<Player> players = new ArrayList<>();;

    public Arena(String name) {

        this.name = name;

        game = new Game(this);

    }

    public void reset(){
        arenaStage = ArenaStage.RESET;
    }

    public void join(Player player){
        if (ArenaList.get(player) != null){
            ChatUtil.sendMessage(player, "Вы уже на арене!");
            return;
        }
        if (arenaStage == ArenaStage.CLOSED) {
            ChatUtil.sendMessage(player, "Арена закрыта!");
            return;
        }
//onJoinLocation.put(player, player.getLocation());
//        player.teleport(lobby);
        players.add(player);
        sendArenaMessage(player.getDisplayName() + " присоединился!");
        if (players.size() >= minPlayers && arenaStage != ArenaStage.CLOSED) {
            startGame();
        }
    }

    private void startGame(){

        arenaStage = ArenaStage.STARTING;
        saveExpArena();

        new BukkitRunnable() {

            int ctr = timeToStart;

            @Override
            public void run() {
                if (ctr <=0) {
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
                if (players.size() < 1){
                    arenaStage = ArenaStage.WAITING;
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L,20L);


    }

    public void leave(Player player){
//        player.teleport(onJoinLocation.get(player));
//        player.teleport(onJoinLocation.get(player));
        if (arenaStage == ArenaStage.WAITING){
            player.setExp(playerExp.get(player));
            player.setLevel(playerExp2.get(player));
        }
 //       onJoinLocation.remove(player);
        players.remove(player);
        sendArenaMessage(player.getDisplayName() + " отключился!");
        if (players.size() < 1 && arenaStage == ArenaStage.STARTING){
            arenaStage = ArenaStage.WAITING;
        }
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
            playerExp2.put(player,player.getLevel());
            player.setExp(0.0f);
        }
    }
    public void setExpArena(){
        for (Player player : players){
            player.setExp(playerExp.get(player));
            player.setLevel(playerExp2.get(player));
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

}