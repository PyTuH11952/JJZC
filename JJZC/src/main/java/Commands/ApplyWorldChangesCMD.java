package Commands;

import Arena.Arena;
import Arena.ArenaList;
import Utils.ChatUtil;
import com.mimikcraft.mcc.Main;
import com.mimikcraft.mcc.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApplyWorldChangesCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player))
            return true;
        Player player = (Player) commandSender;

        if(!player.isOnline()){
            ChatUtil.sendMessage(player, Messages.noPerm);
            return  true;
        }

        File folder = new File(Main.getInstance().getDataFolder().getAbsolutePath());

        File file = new File(folder.getAbsolutePath() + "/Locations.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        List<Location> chestCords = new ArrayList<>();

        World world = Bukkit.getWorld("zombie");
        for (String locationKey : config.getKeys(false)) {
            ConfigurationSection locationSection = config.getConfigurationSection(locationKey);
            List<String> chestsCordsStringList = locationSection.getStringList("chests");
            for (String chestsCordsString : chestsCordsStringList) {
                String[] chestCordsString = chestsCordsString.split(" ");
                double x = Double.parseDouble(chestCordsString[0]);
                double y = Double.parseDouble(chestCordsString[1]);
                double z = Double.parseDouble(chestCordsString[2]);
                chestCords.add(new Location(world, x, y, z));
            }
        }
        for(Location chestLoc : chestCords){
            if(chestLoc.getBlock().getType() == Material.CHEST) {
                Chest chest = (Chest) chestLoc.getBlock().getState();
                chest.getInventory().clear();
            } else if (chestLoc.getBlock().getType() == Material.BARREL) {
                Barrel barrel = (Barrel) chestLoc.getBlock().getState();
                barrel.getInventory().clear();
            } else {
                Bukkit.broadcastMessage("Обнаружен блок, не являющийся бочкой или сундуком! " + chestLoc);
            }
        }
        ChatUtil.sendMessage(player, "&aУспешно очищено &e" + chestCords.size() + " &aбочек и сундуков!");
        return  true;
    }
}