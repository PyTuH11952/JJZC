package Events;

import Arena.Arena;
import Arena.ArenaList;
import Arena.ArtifactsTypes;
import com.mimikcraft.mcc.Main;
import io.lumine.mythic.bukkit.events.MythicMobInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteractMov(MythicMobInteractEvent e){
        boolean чо = true;
        if (чо){
            if(e.getActiveMob().getFaction().equals("Artifact")){
                String artefactName = e.getActiveMob().getName().split("_")[0];
                int artefactLevel = Integer.parseInt(e.getActiveMob().getName().split("_")[1]);
                Player player = e.getPlayer();
                Arena arena = ArenaList.get(player.getWorld().getName());
                switch (artefactName){
                    case "von":
                        new BukkitRunnable(){
                            @Override
                            public void run(){
                                if(player.getHealth() <= 5 + artefactLevel){
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give @e[distance=0..3,tag=zombie,type=zombie] instant_health");
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give @e[distance=0..3,tag=zombie,type=!zombie] instant_damage");
                                }
                            }
                        }.runTaskTimer(Main.getInstance(), 0L, 20L);
                        break;
                    case "strength":
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, artefactLevel));
                        break;
                    case "speed":
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, artefactLevel));
                        break;
                    case "jumpboost":
                        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, artefactLevel));
                        break;
                    case "kachumber":
                        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue() + 2*artefactLevel);
                        break;
                    case "bread":
                        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.1 * artefactLevel);
                        break;
                    case "oil":
                        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.2 * artefactLevel);
                        break;
                    case "doublejump":
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tdj" + player.getName() + " enable");
                        break;
                    case "regeneration":
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, artefactLevel));
                        break;
                    case "pill":
                        new BukkitRunnable(){
                            @Override
                            public void run(){
                                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, artefactLevel));
                            }
                        }.runTaskTimer(Main.getInstance(), 0L, 600L);
                        break;
                    case "bones":
                        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2);
                        break;
                    case "totem":
                        arena.getGame().lifesCount += artefactLevel;
                        break;
                }
                arena.getPlayers().get(player).put(ArtifactsTypes.valueOf(artefactName.toUpperCase()), artefactLevel);
            }
        }
    }
}
