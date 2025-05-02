package JustJoyDEV.Arena.Events;

import Arena.*;
import com.mimikcraft.mcc.Main;
import io.lumine.mythic.bukkit.events.MythicMobInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteractMob(MythicMobInteractEvent e){
        if(e.getActiveMob().getFaction().equals("Artifact")){
            String artefactName = e.getActiveMob().getType().getInternalName().split("_")[0];
            int artefactLevel = Integer.parseInt(e.getActiveMob().getType().getInternalName().split("_")[1]);
            Player player = e.getPlayer();
            Arena arena = ArenaList.get(player.getWorld().getName());
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
            player.sendTitle("Вы получили артефакт!","");
            String artefactLevelString = "";

            switch (artefactLevel){
                case 1:
                    artefactLevelString = "I";
                    break;
                case 2:
                    artefactLevelString = "II";
                    break;
                case 3:
                    artefactLevelString = "III";
                    break;
            }
            switch (artefactName){
                case "von":
                    arena.sendArenaMessage("&aИгрок &e" + player.getDisplayName() + " &aполучил артефакт" + " &eвонючка " + artefactLevelString);
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            if(arena.getArenaStage() != ArenaStages.IN_PROCESS){
                                cancel();
                            }
                            if(player.getHealth() <= 5 + artefactLevel){
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give @e[distance=0..3,tag=zombie,type=zombie] instant_health");
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "effect give @e[distance=0..3,tag=zombie,type=!zombie] instant_damage");
                            }
                        }
                    }.runTaskTimer(Main.getInstance(), 0L, 20L);
                    break;
                case "strength":
                    arena.sendArenaMessage("&aИгрок &e" + player.getDisplayName() + " &aполучил артефакт" + " &eсила " + artefactLevelString);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, artefactLevel));
                    break;
                case "speed":
                    arena.sendArenaMessage("&aИгрок &e" + player.getDisplayName() + " &aполучил артефакт" + " &eскорость " + artefactLevelString);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, artefactLevel));
                    break;
                case "jumpboost":
                    arena.sendArenaMessage("&aИгрок &e" + player.getDisplayName() + " &aполучил артефакт" + " &eпрыгучесть " + artefactLevelString);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, artefactLevel));
                    break;
                case "kachumber":
                    arena.sendArenaMessage("&aИгрок &e" + player.getDisplayName() + " &aполучил артефакт" + " &eкачумбер " + artefactLevelString);
                    player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue() + 2*artefactLevel);
                    break;
                case "bread":
                    arena.sendArenaMessage("&aИгрок &e" + player.getDisplayName() + " &aполучил артефакт" + " &eжировой хлеб " + artefactLevelString);
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.1 * artefactLevel);
                    break;
                case "oil":
                    arena.sendArenaMessage("&aИгрок &e" + player.getDisplayName() + " &aполучил артефакт" + " &eкитовое масло " + artefactLevelString);
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * 0.2 * artefactLevel);
                    break;
                case "doublejump":
                    arena.sendArenaMessage("&aИгрок &e" + player.getDisplayName() + " &aполучил артефакт" + " &eдвойной прыжок");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tdj " + player.getName() + " enable");
                    break;
                case "regeneration":
                    arena.sendArenaMessage("&aИгрок &e" + player.getDisplayName() + " &aполучил артефакт" + " &eрегенерация " + artefactLevelString);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, artefactLevel));
                    break;
                case "pill":
                    arena.sendArenaMessage("&aИгрок &e" + player.getDisplayName() + " &aполучил артефакт" + " &eвкусная таблетка " + artefactLevelString);
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            if(arena.getArenaStage() != ArenaStages.IN_PROCESS){
                                cancel();
                            }
                            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, artefactLevel));
                        }
                    }.runTaskTimer(Main.getInstance(), 0L, 600L);
                    break;
                case "bones":
                    arena.sendArenaMessage("&aИгрок &e" + player.getDisplayName() + " &aполучил артефакт" + " &eхрупкие кости");
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2);
                    break;
                case "totem":
                    arena.sendArenaMessage("&aИгрок &e" + player.getDisplayName() + " &aполучил артефакт" + " &eтотем бессмертия " + artefactLevelString);
                    arena.getGame().setLifesCount(arena.getGame().getLifesCount()+artefactLevel);
                    break;
            }
            arena.getPlayers().get(player).add(new Artifact(ArtifactsTypes.valueOf(artefactName.toUpperCase()), artefactLevel));
        }
    }
}
