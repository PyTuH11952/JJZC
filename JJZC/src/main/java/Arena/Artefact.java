package Arena;

import Utils.ChatUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Artefact {
    private int level;

    private String name;

    public void spawn(World world, Location location){
        Entity entity = world.spawnEntity(location, EntityType.SHULKER);
        entity.setGravity(false);
        entity.setSilent(true);
        entity.setInvulnerable(true);
        entity.setCustomName(name);
    }
    public void get(Player player){
        player.sendTitle("Вы подобрали артефакт!", "");
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
    }

}
class von extends Artefact{


}

