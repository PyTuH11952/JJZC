package com.mimikcraft.mcc;

import Arena.Arena;
import Commands.ForceStartCMD;
import Commands.JoinCMD;
import Commands.TestCMD;
import org.bukkit.plugin.java.JavaPlugin;
import Arena.ArenaList;
import Commands.LeaveCMD;

public final class Main extends JavaPlugin {

    private static Main instance;

    private final int maxarenas = 3;

    @Override
    public void onEnable() {
        getServer().getPluginCommand("join").setExecutor(new JoinCMD());
        getServer().getPluginCommand("leave").setExecutor(new LeaveCMD());
        getServer().getPluginCommand("forcestart").setExecutor(new ForceStartCMD());
        getServer().getPluginCommand("setlocation").setExecutor(new TestCMD());
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

    public int getMaxarenas() {
        return maxarenas;
    }
}
