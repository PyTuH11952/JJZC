package com.mimikcraft.mcc;

import Arena.Arena;
import Commands.*;
import Events.*;
import Party.Party;
import Utils.EmptyChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import Arena.ArenaList;

import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {

    private static Main instance;

    private final int maxArenas = 3;

    private List<Party> partyes = new ArrayList<>();


    @Override
    public void onEnable() {
        WorldCreator wc = new WorldCreator("zombie").generator(new EmptyChunkGenerator());
        wc.createWorld();
        getServer().getPluginCommand("join").setExecutor(new JoinCMD());
        getServer().getPluginCommand("leave").setExecutor(new LeaveCMD());
        getServer().getPluginCommand("launch").setExecutor(new LaunchCMD());
        getServer().getPluginCommand("gamesettings").setExecutor(new GameSettingsCMD());
        getServer().getPluginCommand("revive").setExecutor(new ReviveCMD());
        getServer().getPluginCommand("addlife").setExecutor(new AddLifeCMD());
        getServer().getPluginCommand("setspawns").setExecutor(new SetSpawnsCMD());
        getServer().getPluginCommand("getchest").setExecutor(new GetChestCMD());
        getServer().getPluginCommand("forcestart").setExecutor(new ForceStartCMD());
        getServer().getPluginCommand("choosestructurechanges").setExecutor(new ChooseStructureChangesCMD());
        getServer().getPluginCommand("setstructurechanges").setExecutor(new SetStructureChangesCMD());
        getServer().getPluginCommand("arenakick").setExecutor(new KickCMD());
        getServer().getPluginCommand("party").setExecutor(new PartyCMD());
        getServer().getPluginCommand("partychat").setExecutor(new PartyChatCMD());
        getServer().getPluginCommand("applyWorldChanges").setExecutor(new ApplyWorldChangesCMD());
        getServer().getPluginCommand("test").setExecutor(new TestCMD());
        getServer().getPluginManager().registerEvents(new KillsEventListener(), this);
        getServer().getPluginManager().registerEvents(new BlockEventListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
 //       getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new MobSpawnEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveEventListener(), this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) { //
            new Expansion(this).register(); //
        }

        instance = this;
        for(int i = 0; i <= maxArenas; i++){
            Arena arena = new Arena("arena"+i);
            ArenaList.addarena(arena);
        }
    }

    @Override
    public void onDisable() {
        instance = null;
    }
    public static Main getInstance() {
        return instance;
    }

    public List<Party> getPartyes() {
        return partyes;
    }
}
