package Arena;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaList {

    private static final List<Arena> arenas = new ArrayList<>();


    public static void addarena(Arena arena){
        arenas.add(arena);
    }

    public static Arena get(String name) {

        for (Arena arena : arenas) {
            if (arena.getName().equals(name)){
                return arena;
            }
        }

        return null;

    }

    public static Arena get(Player player) {

        for (Arena arena : arenas) {
            if (arena.getPlayers().containsKey(player)) {
                return arena;
            }
        }

        return null;

    }
    public static boolean hasArena(String name) {
        for (Arena arena : arenas) {
            if (arena.getName().equals(name)) {
                return true;
            }
        }
        return  false;
    }
    public static boolean hasArena(Player player) {
        for (Arena arena : arenas) {
            if (arena.getPlayers().containsKey(player)) {
                return true;
            }
        }
        return  false;
    }

    public static List<Arena> getAvailable(Player player){
        List<Arena> temp = new ArrayList<>();
        for(Arena arena : arenas){
            if(arena.getArenaStage() == ArenaStages.IN_PROCESS || arena.getArenaStage() == ArenaStages.STARTING){
                if(arena.canJoin(player)){
                    temp.add(arena);
                }
            }
        }
        return temp;
    }

    public static Arena getFreeArena(){
        for(Arena arena: arenas){
            if(arena.getArenaStage() == ArenaStages.FREE){
                return arena;
            }
        }
        return null;
    }

}
