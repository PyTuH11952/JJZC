package Arena;

import Party.PartyList;
import Utils.ChatUtil;
import Utils.RemoveItemUtil;
import com.mimikcraft.mcc.ExecutableApi;
import com.mimikcraft.mcc.Main;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
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

    private final Map<Player, Map<Integer, ItemStack>> playerInventory = new HashMap<>();

    private ArenaStages arenaStage = ArenaStages.FREE;

    private Game game;
    private ArenaLocation location;

    private final Map<Player, List<Artifact>> players = new HashMap<>();
    private final List<Player> ghosts = new ArrayList<>();
    private final List<Player> leavedPlayers = new ArrayList<>();

    private Player host;

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
        Bukkit.unloadWorld(Bukkit.getWorld(name), true);
        World source = Bukkit.getWorld("zombie");
        File sourceFolder = source.getWorldFolder();
        File file = new File("/home/container/"+name);
        copyWorld(sourceFolder, file);
        WorldCreator wc = new WorldCreator(name).generator(new EmptyChunkGenerator());
        wc.createWorld();
        game = new Game(this);
        players.clear();
        arenaStage = ArenaStages.FREE;
    }

    public void join(Player player){
        if (ArenaList.get(player) != null){
            ChatUtil.sendMessage(player, "Вы уже на арене!");
            return;
        }
        if (arenaStage == ArenaStages.RESET || arenaStage == ArenaStages.GAME_ENDED) {
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
        if (PartyList.hasParty(player)){
            if (PartyList.getParty(player).getHost() == player && PartyList.getParty(player).getPartyPlayers().size() > (maxPlayers-players.size())){
                ChatUtil.sendMessage(player, "На арене недостаточно места для всех учатников пати!");
                return;
            }
        }
        playerExp.put(player,player.getExp());
        playerLvl.put(player,player.getLevel());
        player.setExp(0.0f);
        onJoinLocation.put(player, player.getLocation());
        playerInventory.put(player, new HashMap<>());
        for(int i = 0; i < player.getInventory().getSize(); i++){
            playerInventory.get(player).put(i, player.getInventory().getItem(i));
        }
        players.put(player, new ArrayList<>());
        sendArenaMessage(player.getDisplayName() + " присоединился!");
        player.getInventory().clear();
        player.getActivePotionEffects().clear();
        if (arenaStage == ArenaStages.STARTING || arenaStage == ArenaStages.FREE){
            player.teleport(new Location(Bukkit.getWorld(name), location.getLobbyLocation().getX(),location.getLobbyLocation().getY(), location.getLobbyLocation().getZ()));
        } else{
            if(getGame().getTrain() == null){
                player.teleport(new Location(Bukkit.getWorld(name), location.getSpawnLocation().getX(),location.getSpawnLocation().getY(), location.getSpawnLocation().getZ()));
            } else{
                player.teleport(getGame().getTrain().newSpawn);
            }
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
        if (players.size() == 1) {
            host = player;
            ExecutableApi.setExecutableItem(host, "hostitem1", 1, 8);
            ExecutableApi.setExecutableItem(host, "hostitem2", 1, 7);
            startGame();
        }

        ExecutableApi.giveExecutableItem(player, "hubitem1", 1);
        ExecutableApi.giveExecutableItem(player, "hubitem2", 1);
        ExecutableApi.giveExecutableItem(player, "hubitem3", 1);
        ExecutableApi.giveExecutableItem(player, "hubitem4", 1);

        if (PartyList.hasParty(player)){
            if (PartyList.getParty(player).getHost() == player){
                for (Player partyPlayer : PartyList.getParty(player).getPartyPlayers()){
                    if (PartyList.getParty(player).getHost() != partyPlayer) {
                        join(partyPlayer);
                    }
                }
            }
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
                    arenaStage = ArenaStages.FREE;
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L,20L);
    }

    public void leave(Player player){
        if (arenaStage == ArenaStages.STARTING){
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
        if(players.size() == 1){
            for(Map.Entry<Player, List<Artifact>> entry : players.entrySet()){
                host = entry.getKey();
                ExecutableApi.setExecutableItem(host, "hostitem1", 1, 8);
                ExecutableApi.setExecutableItem(host, "hostitem2", 1, 7);
                break;
            }
        }
        player.teleport(onJoinLocation.get(player));
        game.removeBossBar(player);
        for(Map.Entry<Integer, ItemStack> entry : playerInventory.get(player).entrySet()){
            player.getInventory().setItem(entry.getKey(), entry.getValue());
        }
        player.getActivePotionEffects().clear();
        if (PartyList.hasParty(player)){
            if (PartyList.getParty(player).getHost() == player){
                for (Player partyPlayer : PartyList.getParty(player).getPartyPlayers()) {
                    if (partyPlayer != PartyList.getParty(player).getHost() && ArenaList.hasArena((partyPlayer))) {
                        ArenaList.get(partyPlayer).leave(partyPlayer);
                    }
                }
            }
        }
    }

    public void playerDie(Player player){
        ghosts.add(player);
        player.setGameMode(GameMode.SPECTATOR);
        player.sendTitle(ChatColor.translateAlternateColorCodes('&', "&cВы умерли!"), "");
        new BukkitRunnable(){
            @Override
            public void run(){
                if(!ghosts.contains(player)){
                    cancel();
                }
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give @a[distance=0..3] instant_health");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give @e[distance=0..3, tag=zombie] slowness");
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
        if (ghosts.size() == players.size()){
            game.endGame(false);
        }
    }

    public void reviveRandom(Player player){
        if(ghosts.isEmpty()){
            ChatUtil.sendMessage(player, "&cВсе игроки живы!");
            player.closeInventory();
            return;
        }
        ItemStack item = new ItemStack(Material.AIR);
        Optional<ExecutableItemInterface> eiOpt = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem("material4");
        if (eiOpt.isPresent()) {
            item = eiOpt.get().buildItem(5, Optional.empty(), Optional.of(player));
        }
        if(player.getInventory().containsAtLeast(item, 7)){
            RemoveItemUtil.remove(player, item, 7);
            Player ghost = ghosts.get((int)(Math.random() * ghosts.size()));
            ghosts.remove(ghost);
            ghost.setGameMode(GameMode.ADVENTURE);
            ghost.teleport(location.getSpawnLocation());
            ghost.sendTitle(ChatColor.translateAlternateColorCodes('&', "&aВы воскрешены!"), "");
        } else {
            ChatUtil.sendMessage(player, "&cНедостаточно материала!");
            player.closeInventory();
        }
    }


    public void addLife(Player player){
        Optional<ExecutableItemInterface> eiOpt = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem("material5");
        ItemStack item = new ItemStack(Material.AIR);
        if (eiOpt.isPresent()) {
            item = eiOpt.get().buildItem(5, Optional.empty(), Optional.of(player));
        }
        if (player.getInventory().containsAtLeast(item,5)){
            RemoveItemUtil.remove(player, item, 5);
            getGame().setLifesCount(getGame().getLifesCount()+1);
            player.closeInventory();
            sendArenaMessage("&aИгрок &e" + player + " &aскрафтил дополнительную жизнь!");
        } else{
            ChatUtil.sendMessage(player, "&cНедостаточно материала!");
            player.closeInventory();
        }
    }

    public boolean canJoin(Player player) {
        if(game.getHardLevel() == 1) return true;
        if(!player.hasPermission("loc" + (location.getLocationType().ordinal() + 1) + "." + (game.getHardLevel() - 1))){
            ChatUtil.sendMessage(player, "&cНе удалось определить локацию");
            return false;
        }else return true;
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

    public String getName() {
        return name;
    }


    public Map<Player, List<Artifact>> getPlayers() {
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

    public List<Player> getGhosts(){
        return ghosts;
    }

    public Player getHost() {
        return host;
    }

}