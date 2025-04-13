package Commands;

import Events.BlockEventListener;
import Events.Editor;
import Utils.ChatUtil;
import com.mimikcraft.mcc.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import java.util.Map;

public class SetStructureChangesCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player)){
            System.out.println("Иди нафиг, консоль!");
            return true;
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

        Player player = (Player) commandSender;
        if(args.length == 2) {
            for (Editor editor : BlockEventListener.editors) {
                if (editor.player.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                    String locName = editor.locName;
                    int stage = editor.stage;

                    if (config.getConfigurationSection(locName.toLowerCase()) == null) {
                        config.createSection(locName.toLowerCase());

                    }
                    if (config.getConfigurationSection(locName.toLowerCase() + ".stages") == null) {
                        config.createSection(locName.toLowerCase() + ".stages");
                    }
                    if (config.getConfigurationSection(locName.toLowerCase() + ".stages.stage" + stage) == null) {
                        config.createSection(locName.toLowerCase() + ".stages.stage" + stage);
                    }
                    if (config.getConfigurationSection(locName.toLowerCase() + ".stages.stage" + stage + ".structureChanges") == null) {
                        config.createSection(locName.toLowerCase() + ".stages.stage" + stage + ".structureChanges");
                    }
                    ConfigurationSection stageSection = config.getConfigurationSection(locName.toLowerCase() + ".stages.stage" + stage);
                    List<String> changesStr = new ArrayList<>();
                    for (Map.Entry<Location, Material> entry : editor.changes.entrySet()) {
                        String coordsStr = entry.getKey().getX() + " " + entry.getKey().getY() + " " + entry.getKey().getZ() + " ";
                        changesStr.add(coordsStr + ":" + entry.getValue().name());
                    }
                    stageSection.set("structureChanges", changesStr);
                    try {
                        config.save(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    BlockEventListener.editors.remove(editor);
                    return true;
                }
            }
            ChatUtil.sendMessage(player, "&cСначала пропишите команду /choosestructurechanges и сделайте изменения в локации");
        return  true;
        }
        if (args.length == 10) {
            int x1 = Integer.parseInt(args[0]);
            int y1 = Integer.parseInt(args[1]);
            int z1 = Integer.parseInt(args[2]);

            int x2 = Integer.parseInt(args[3]);
            int y2 = Integer.parseInt(args[4]);
            int z2 = Integer.parseInt(args[5]);

            int diffX = Math.abs(x1 - x2);
            int diffY = Math.abs(y1 - y2);
            int diffZ = Math.abs(z1 - z2);

            int actX = (x1 - x2) / diffX;
            int actY = (y1 - y2) / diffY;
            int actZ = (z1 - z2) / diffZ;

            List<String> changesStr = new ArrayList<>();

            for (int iZ = 0; iZ < diffZ + 1; iZ++) {
                int resZ = z1 - actZ * iZ;
                for (int iY = 0; iY < diffY + 1; iY++) {
                    int resY = y1 - actY * iY;

                    for (int iX = 0; iX < diffX + 1; iX++) {
                        int resX = x1 - actX * iX;
                        Block block = player.getWorld().getBlockAt(resX, resY, resZ);
                        Block sourceBlock = Bukkit.getWorld("zombie").getBlockAt(resX, resY, resZ);
                        if (block.getType() != sourceBlock.getType()){
                            String blockChange = resX + " " + resY + " " + resZ + " :" + block.getType();
                            changesStr.add(blockChange);

                        }

                    }

                }
            }
            ConfigurationSection locationSection = config.getConfigurationSection(args[6]);
            ConfigurationSection section = locationSection.getConfigurationSection(args[7]);
            ConfigurationSection sect;
            if (args[7].equals("stages")){
                sect = section.getConfigurationSection("stage" + args[8]);
            } else {
                sect = section.getConfigurationSection(args[8]);
            }

            if (Integer.parseInt(args[8]) > 1 && args[9].equals("true")) {
                List<String> previousChanges = new ArrayList<>();
                for (int i = (Integer.parseInt(args[8])-1); i >= 1; i--) {
                    if (args[7].equals("stages")){
                        ConfigurationSection previousSection = section.getConfigurationSection("stage" + i);
                        previousChanges.addAll(previousSection.getStringList("structureChanges"));
                    } else {
                        ConfigurationSection previousSection = section.getConfigurationSection("" + i);
                        previousChanges.addAll(previousSection.getStringList("structureChanges"));
                    }
                }
                changesStr.removeIf(previousChanges::contains);
            }
            sect.set("structureChanges", changesStr);

            try {
                config.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ChatUtil.sendMessage(player, "&aУспешно записаны изменения &e" + changesStr.size() + " &aблоков!");
                    return true;
        }
        if(args.length == 1){
            if (args[0].equals("help")){
                ChatUtil.sendMessage(player, "&cИспользование: /setStructureChanges <локация> <номер>");
                ChatUtil.sendMessage(player, "&cИспользование: /setStructureChanges <x1> <y1> <z1> <x2> <y2> <z2> <локация> <stages/waves> <номер> <true/false> (убирать изменения в предыдыдущих stages/waves. По умолчанию false)");
            }
        }
        return true;
    }
}
