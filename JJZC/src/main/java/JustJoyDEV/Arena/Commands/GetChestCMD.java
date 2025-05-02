package JustJoyDEV.Arena.Commands;

import Utils.ChatUtil;
import com.mimikcraft.mcc.Main;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

public class GetChestCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player)commandSender;
        if(args.length != 7){
            ChatUtil.sendMessage(player, "&cИспользование: /getChests <x1> <y1> <z1> <x2> <y2> <z2> <location>");
            return true;
        }
        int x1 = Integer.parseInt(args[0]);
        int y1 = Integer.parseInt(args[1]);
        int z1 = Integer.parseInt(args[2]);
        int x2 = Integer.parseInt(args[3]);
        int y2 = Integer.parseInt(args[4]);
        int z2 = Integer.parseInt(args[5]);
        String locationName = args[6];

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

        if(config.getConfigurationSection(locationName) == null){
            config.createSection(locationName);
        }
        ConfigurationSection locationSection = config.getConfigurationSection(locationName);
        List<String> tempList = new ArrayList<>();
        List<String> tempList2 = locationSection.getStringList("chests");
        for(String chestCords : tempList2){
            tempList.add(chestCords);
        }
        int diffX = Math.abs(x1-x2);
        int diffY = Math.abs(y1-y2);
        int diffZ = Math.abs(z1-z2);

        int actX = (x1-x2)/diffX;
        int actY = (y1-y2)/diffY;
        int actZ = (z1-z2)/diffZ;
        for(int iZ = 0; iZ < diffZ+1 ; iZ++){
            int resZ = z1 - actZ * iZ;
            for(int iY = 0; iY < diffY+1 ; iY++) {
            int resY = y1 - actY * iY;

            for (int iX = 0; iX < diffX+1; iX++) {
                int resX = x1 - actX * iX;
                Block block = player.getWorld().getBlockAt(resX, resY, resZ);
                if (block.getType() == Material.CHEST || block.getType() == Material.BARREL) {
                    String res = resX + " " + resY + " " + resZ;
                    tempList.add(res);
                }
            }
            }
        }

        config.set(locationName + ".chests", tempList);
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ChatUtil.sendMessage(player, "&aУспешно добавлено &e" + (tempList.size()-tempList2.size()) + "&a блоков в конфиг!");
        return  true;
    }
}