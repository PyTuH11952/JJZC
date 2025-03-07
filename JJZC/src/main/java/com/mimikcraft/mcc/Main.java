package com.mimikcraft.mcc;

import Arena.Arena;
import Commands.*;
import Events.*;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import Arena.ArenaList;

public final class Main extends JavaPlugin {

    private static Main instance;

    private final int maxArenas = 3;


    @Override
    public void onEnable() {
        WorldCreator wc = new WorldCreator("zombie");
        wc.createWorld();
        getServer().getPluginCommand("join").setExecutor(new JoinCMD());
        getServer().getPluginCommand("leave").setExecutor(new LeaveCMD());
        getServer().getPluginCommand("launch").setExecutor(new LaunchCMD());
        getServer().getPluginCommand("revive").setExecutor(new ReviveCMD());
        getServer().getPluginCommand("addlife").setExecutor(new AddLifeCMD());
        getServer().getPluginCommand("setspawns").setExecutor(new SetSpawnsCMD());
        getServer().getPluginCommand("getchest").setExecutor(new GetChestCMD());
        getServer().getPluginCommand("choosestructurechanges").setExecutor(new ChooseStructureChangesCMD());
        getServer().getPluginCommand("setstructurechanges").setExecutor(new SetStructureChangesCMD());
        getServer().getPluginManager().registerEvents(new KillsEventListener(), this);
        getServer().getPluginManager().registerEvents(new BlockEventListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new MobSpawnEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionEventListener(), this);

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
}
