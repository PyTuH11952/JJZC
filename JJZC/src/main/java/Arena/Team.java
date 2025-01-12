package Arena;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private final String name;
    private Location lobby;

    private final List<Player> players = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
