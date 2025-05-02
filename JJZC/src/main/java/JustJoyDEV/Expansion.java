package JustJoyDEV;

import Arena.ArenaList;
import com.mimikcraft.mcc.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Expansion extends PlaceholderExpansion{

    private final Main plugin; //

    public Expansion(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors()); //
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "JJZC";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion(); //
    }

    @Override
    public boolean persist() {
        return true; //
    }


    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {

        if (params.equalsIgnoreCase("zombieKills")) {
            if (ArenaList.get(player) != null){
                if (ArenaList.get(player).getGame().getPlayerKills().get(player) == null) {
                    return "0";
                } else {
                    return String.valueOf(ArenaList.get(player).getGame().getPlayerKills().get(player));
                }
            } else return "0";

        }

        if (params.equalsIgnoreCase("lifesCount")) {
            if (ArenaList.get(player) != null){
                return String.valueOf(ArenaList.get(player).getGame().getLifesCount());
            }else return "0";
        }

        if (params.equalsIgnoreCase("hardLevel")) {
            if (ArenaList.get(player) != null){
                return String.valueOf(ArenaList.get(player).getGame().getHardLevel());
            } else return "0";
        }

        if (params.equalsIgnoreCase("gameProgress")) {
            if (ArenaList.get(player) != null){
                return ArenaList.get(player).getGame().getGameProgress() + "%";
            } return "0%";
        }
        if (params.equalsIgnoreCase("glowingTime")) {
            if (ArenaList.get(player) != null){
                if (!ArenaList.get(player).getGame().isGlowingTimer()){
                    return "отключено";
                }
                return ArenaList.get(player).getGame().getGlowingTime() + " сек.";
            } else return "0";
        }

        return null;
    }
}