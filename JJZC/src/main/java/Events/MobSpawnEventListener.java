package Events;

import Arena.Arena;
import Arena.ArenaList;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MobSpawnEventListener implements Listener {

    @EventHandler
    public void onEntityDeath(MythicMobSpawnEvent e) {
        if (e.getMob().hasFaction()) {
            Arena arena = ArenaList.get(e.getEntity().getWorld().getName());
            if (e.getMob().getFaction().equals("Zombie") || e.getMob().getFaction().equals("BombZombie") || e.getMob().getFaction().equals("Kaka")) {
                arena.getGame().mobs.add(e.getEntity());
                arena.getGame().sendBossBar();
                if (e.getSpawnReason() == SpawnReason.SUMMON){
                    arena.getGame().aliveZombies++;
                    arena.getGame().setZombiesCount(arena.getGame().getZombiesCount()+1);
                    arena.getGame().setSpawnedZombies(arena.getGame().getSpawnedZombies()+1);
                }
            }


        }
    }
}
