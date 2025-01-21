package Arena;

import com.mimikcraft.mcc.Main;
import io.lumine.mythic.bukkit.utils.config.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ArenaLocation {

    public ArenaLocation(){
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
    }

    public enum LocationTypes {
        HOSPITAL,
        MALL,
        GARAGE,
        FACTORY,
        METRO
    }
}


class Stage{
    public Stage(int num){

    }
}


