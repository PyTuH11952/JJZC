package Arena;

import Party.PartyList;
import Utils.ChatUtil;
import Utils.EmptyChunkGenerator;
import Utils.RemoveItemUtil;
import com.mimikcraft.mcc.ExecutableApi;
import com.mimikcraft.mcc.Main;
import com.mimikcraft.mcc.Messages;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import io.r2dbc.spi.Result;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
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

    private final Map<UUID, Location> onJoinLocation = new HashMap<>();

    private final Map<UUID, Float> playerExp = new HashMap<>();

    private final Map<UUID, Integer> playerLvl = new HashMap<>();

    private final Map<UUID, Map<Integer, ItemStack>> playerInventory = new HashMap<>();

    private ArenaStages arenaStage = ArenaStages.FREE;

    private Game game;
    private ArenaLocation location;

    private final Map<UUID, List<Artifact>> players = new HashMap<>();
    private final List<UUID> ghosts = new ArrayList<>();
    private final List<UUID> leavedPlayers = new ArrayList<>();

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
        for (UUID uuid : players.keySet()){
            Player player = Bukkit.getPlayer(uuid);
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
            ChatUtil.sendMessage(player, Messages.alreadyOnArena);
            return;
        }
        if (arenaStage == ArenaStages.RESET || arenaStage == ArenaStages.GAME_ENDED) {
            ChatUtil.sendMessage(player, Messages.arenaClosed);
            return;
        }
        if (players.size() >= maxPlayers){
            ChatUtil.sendMessage(player, Messages.arenaFull);
            return;
        }
        if(leavedPlayers.contains(player)){
            ChatUtil.sendMessage(player, Messages.leavedFromArena);
            return;
        }
        if (PartyList.hasParty(player)){
            if (PartyList.getParty(player).getHost() == player && PartyList.getParty(player).getPartyPlayers().size() > (maxPlayers-players.size())){
                ChatUtil.sendMessage(player, Messages.arenaFullForParty);
                return;
            }
        }
        playerExp.put(player.getUniqueId(),player.getExp());
        playerLvl.put(player.getUniqueId(),player.getLevel());
        player.setExp(0.0f);
        onJoinLocation.put(player.getUniqueId(), player.getLocation());
        playerInventory.put(player.getUniqueId(), new HashMap<>());
        for(int i = 0; i < player.getInventory().getSize(); i++){
            playerInventory.get(player.getUniqueId()).put(i, player.getInventory().getItem(i));
        }
        players.put(player.getUniqueId(), new ArrayList<>());
        sendArenaMessage(player.getDisplayName() + " " + Messages.arenaJoin);
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
            for (UUID otherPlayerUuid : getPlayers().keySet()) {
                Player otherPlayer = Bukkit.getPlayer(otherPlayerUuid);
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
                for (UUID partyPlayerUuid : PartyList.getParty(player).getPartyPlayers()){
                    Player partyPlayer = Bukkit.getPlayer(partyPlayerUuid);
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
                    sendArenaTitle(Messages.leftUntilStart, "&c&l" + ctr);
                    sendArenaMessage(Messages.leftUntilStart + "&c " + ctr + " &eсекунд...");
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
            player.setExp(playerExp.get(player.getUniqueId()));
            player.setLevel(playerLvl.get(player.getUniqueId()));
        }
        players.remove(player);
        if(arenaStage == ArenaStages.IN_PROCESS){
            leavedPlayers.add(player.getUniqueId());
            sendArenaMessage(player.getDisplayName() + " " + Messages.arenaEscape);
        }else{
            sendArenaMessage(player.getDisplayName() + " " + Messages.arenaLeave);
        }
        if (players.isEmpty() && arenaStage == ArenaStages.STARTING){
            reset();
        }
        if(players.size() == 1){
            for(Map.Entry<UUID, List<Artifact>> entry : players.entrySet()){
                host = Bukkit.getPlayer(entry.getKey());
                ExecutableApi.setExecutableItem(host, "hostitem1", 1, 8);
                ExecutableApi.setExecutableItem(host, "hostitem2", 1, 7);
                break;
            }
        }
        player.teleport(onJoinLocation.get(player.getUniqueId()));
        game.removeBossBar(player);
        for(Map.Entry<Integer, ItemStack> entry : playerInventory.get(player.getUniqueId()).entrySet()){
            player.getInventory().setItem(entry.getKey(), entry.getValue());
        }
        player.getActivePotionEffects().clear();
        if (PartyList.hasParty(player)){
            if (PartyList.getParty(player).getHost() == player){
                for (UUID partyPlayerUuid : PartyList.getParty(player).getPartyPlayers()) {
                    Player partyPlayer = Bukkit.getPlayer(partyPlayerUuid);
                    if (partyPlayer != PartyList.getParty(player).getHost() && ArenaList.hasArena((partyPlayer))) {
                        ArenaList.get(partyPlayer).leave(partyPlayer);
                    }
                }
            }
        }
    }

    public void playerDie(Player player){
        ghosts.add(player.getUniqueId());
        player.setGameMode(GameMode.SPECTATOR);
        player.sendTitle(ChatColor.translateAlternateColorCodes('&', Messages.arenaDeath), "");
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
            UUID ghostUuid = ghosts.get((int)(Math.random() * ghosts.size()));
            Player ghost = Bukkit.getPlayer(ghostUuid);
            ghosts.remove(ghost);
            ghost.setGameMode(GameMode.ADVENTURE);
            ghost.teleport(location.getSpawnLocation());
            ghost.sendTitle(ChatColor.translateAlternateColorCodes('&', Messages.arenaRevived), "");
        } else {
            ChatUtil.sendMessage(player, Messages.needMoreItems);
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
            sendArenaMessage("&aИгрок &e" + player + " " + Messages.craftAdditionalLife);
        } else{
            ChatUtil.sendMessage(player, Messages.needMoreItems);
            player.closeInventory();
        }
    }

    public boolean canJoin(Player player) {
        if(game.getHardLevel() == 1) return true;
        if(!player.hasPermission("loc" + (location.getLocationType().ordinal() + 1) + "." + (game.getHardLevel() - 1))){
            ChatUtil.sendMessage(player, Messages.couldntDetermineLocation);
            return false;
        }else return true;
    }


    public void setLocationType(ArenaLocation.LocationTypes locationType){
        location = new ArenaLocation(locationType, arenaWorld);
    }

    public void sendArenaMessage(String message){
        for (UUID uuid : players.keySet()){
            Player player = Bukkit.getPlayer(uuid);
            ChatUtil.sendMessage(player, message);
        }
    }

    public void sendArenaTitle (String message, String subMessage){
        for (UUID uuid : players.keySet()){
            Player player = Bukkit.getPlayer(uuid);
            player.sendTitle(ChatColor.translateAlternateColorCodes('&', message), ChatColor.translateAlternateColorCodes('&', subMessage));
        }
    }

    public void expTimerArena (int value){
        for (UUID uuid : players.keySet()){
            Player player = Bukkit.getPlayer(uuid);
            player.setLevel(value);
        }
    }
    public void expSoundArena (){
        for (UUID uuid : players.keySet()){
            Player player = Bukkit.getPlayer(uuid);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1,1);
        }
    }

    public void setPlayerExp(){
        for (UUID uuid : players.keySet()){
            if(playerExp.get(uuid) == 0 || playerLvl.get(uuid) == 0){
                return;
            }
            Player player = Bukkit.getPlayer(uuid);
            player.setExp(playerExp.get(uuid));
            player.setLevel(playerLvl.get(uuid));
        }
    }

    public String getName() {
        return name;
    }


    public Map<UUID, List<Artifact>> getPlayers() {
        return players;
    }


    public int getMinPlayers() {
        return minPlayers;
    }


    public Map<UUID, Location> getOnJoinLocation() {
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

    public List<UUID> getGhosts(){
        return ghosts;
    }

    public Player getHost() {
        return host;
    }

}