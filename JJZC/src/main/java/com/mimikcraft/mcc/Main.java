package com.mimikcraft.mcc;

import Arena.Arena;
import Commands.JoinCMD;
import Commands.LaunchCMD;
import Utils.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import Arena.ArenaList;
import Commands.LeaveCMD;

import java.io.File;

public final class Main extends JavaPlugin {

    private static Main instance;

    private final int maxarenas = 3;

    @Override
    public void onEnable() {
        WorldCreator wc = new WorldCreator("zombie");
        wc.createWorld();
        getServer().getPluginCommand("join").setExecutor(new JoinCMD());
        getServer().getPluginCommand("leave").setExecutor(new LeaveCMD());
        getServer().getPluginCommand("launch").setExecutor(new LaunchCMD());
        instance = this;
        for(int i = 0 ; i <= maxarenas; i++){
            Arena arena = new Arena("arena"+i);
            ArenaList.addarena(arena);

        }
    }

    @Override
    public void onDisable() {
        instance = null;
        // Plugin shutdown logic
    }
    public static Main getInstance() {
        return instance;
    }
}
