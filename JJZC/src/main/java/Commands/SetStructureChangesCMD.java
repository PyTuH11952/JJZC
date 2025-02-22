package Commands;

import Events.BlockEventListener;
import Events.Editor;
import Utils.ChatUtil;
import com.mimikcraft.mcc.Main;
import org.bukkit.Location;
import org.bukkit.Material;
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
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)){
            System.out.println("Иди нафиг, консоль!");
            return true;
        }
        Player player = (Player) commandSender;
        for(Editor editor : BlockEventListener.editors){
            if(editor.player.getUniqueId().toString().equals(player.getUniqueId().toString())){
                String locName = editor.locName;
                int stage = editor.stage;
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
                if(config.getConfigurationSection(locName.toLowerCase()) == null){
                    config.createSection(locName.toLowerCase());

                }
                if(config.getConfigurationSection(locName.toLowerCase() + ".stages") == null){
                    config.createSection(locName.toLowerCase() + ".stages");
                }
                if(config.getConfigurationSection(locName.toLowerCase() + ".stages.stage" + stage) == null){
                    config.createSection(locName.toLowerCase() + ".stages.stage" + stage);
                }
                if(config.getConfigurationSection(locName.toLowerCase() + ".stages.stage" + stage + ".structureChanges") == null){
                    config.createSection(locName.toLowerCase() + ".stages.stage" + stage + ".structureChanges");
                }
                ConfigurationSection stageSection = config.getConfigurationSection(locName.toLowerCase() + ".stages.stage" + stage);
                List<String> changesStr = new ArrayList<>();
                for(Map.Entry<Location, Material> entry : editor.changes.entrySet()){
                    String coordsStr = entry.getKey().getX() + " " + entry.getKey().getY() + " " + entry.getKey().getZ() + " ";
                    changesStr.add(coordsStr + ":" + entry.getValue().name());
                }
                stageSection.set("structureChanges",  changesStr);
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
        return true;
    }
}
