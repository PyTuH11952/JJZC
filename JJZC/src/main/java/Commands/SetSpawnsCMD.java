package Commands;

import Utils.ChatUtil;
import com.mimikcraft.mcc.Main;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.mechanics.PlayAnimationEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SetSpawnsCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(command instanceof Player)){
            return true;
        }
        Player player = (Player) commandSender;
        if(!player.isOp()){
            ChatUtil.sendMessage(player, "&cУ вас нет прав на эту команду!");
            return true;
        }
        if(args.length == 0){
            ChatUtil.sendMessage(player, "&cВвдеите название локации!");
            return true;
        }
        if(args.length == 1){
            ChatUtil.sendMessage(player, "&cВвдеите номер этапа!");
            return true;
        }
        Collection<ActiveMob> mobs = MythicBukkit.inst().getMobManager().getActiveMobs();
        List<Location> spawners = new ArrayList<>();
        for(ActiveMob mob : mobs){
            if(mob.getFaction().equals("maintenance")){
                AbstractLocation absLoc = mob.getLocation();
                Location spawner = new Location(player.getWorld(), absLoc.getX(), absLoc.getY(), absLoc.getZ());
                spawners.add(spawner);
                mob.setDead();
            }
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
        if(!config.contains(args[0])){
            config.createSection(args[0]);
        }else if(!config.contains(args[0] + ".stages")){
            config.createSection(args[0] + ".stages");
        }else if(!config.contains(args[0] + ".stages.stage" + args[1])){
            config.createSection(args[0] + ".stages.stage" + args[1]);
        }else if(!config.contains(args[0] + ".stages.stage" + args[1] + ".spawners")){
            config.createSection(args[0] + ".stages.stage" + args[1] + ".spawners");
        }
        List<String> locationsStr = new ArrayList<>();
        for(Location spawner : spawners){
            String res = spawner.getX() + " " + spawner.getY() + " " + spawner.getZ();
            locationsStr.add(res);
        }
        config.set(args[0] + ".stages.stage" + args[1] + ".spawners", locationsStr);
        return true;
    }
}
