package com.mimikcraft.mcc;

import Arena.Arena;
import Commands.*;
import Events.KillsEventListener;
import Events.BlockEventListener;
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
        getServer().getPluginCommand("setspawns").setExecutor(new SetSpawnsCMD());
        getServer().getPluginCommand("getchest").setExecutor(new GetChestCMD());
        getServer().getPluginManager().registerEvents(new KillsEventListener(), this);
        getServer().getPluginManager().registerEvents(new BlockEventListener(), this);
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
