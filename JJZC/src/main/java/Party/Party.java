package Party;

import Utils.ChatUtil;
import com.mimikcraft.mcc.Main;
import io.lumine.mythic.bukkit.utils.lib.jooq.impl.QOM;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {
    private List<UUID> partyPlayers = new ArrayList<>();
    private List<UUID> invitedPlayers = new ArrayList<>();
    private static final String prefix = "&0[&eПати&0]&7: ";
    private Player host;

    public void sendPartyMessage(String msg) {
        for (UUID playerUuid : partyPlayers) {
            Player player = Bukkit.getPlayer(playerUuid);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + msg));
        }
    }

    public void invite(Player player, Player sender) {
        invitedPlayers.add(player.getUniqueId());
        ChatUtil.sendMessage(player, "&aИгрок &e" + sender + " &aотправил вам приглашение в пати!");
        ChatUtil.sendMessage(player, "&e/party accept &a- принять запрос в пати");
        ChatUtil.sendMessage(player, "&e/party cancel &a- отклонить запрос в пати");
        ChatUtil.sendMessage(player, "&aЧерез &e60 &aсекунд время приглашения истечет");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (invitedPlayers.contains(player)) {
                    invitedPlayers.remove(player);
                    ChatUtil.sendMessage(player, "&cВремя приглашения истекло!");
                }
            }
        }.runTaskLater(Main.getInstance(), 1200);
    }

    public void join(Player player) {
        if (partyPlayers.size() >= 8) {
            ChatUtil.sendMessage(player, "&cВ пати достигнуто максимальное количество игроков!");
            return;
        }
        if (partyPlayers.contains(player)) {
            ChatUtil.sendMessage(player, "&cВы уже состоите в данном пати!");
            return;
        }
        if (invitedPlayers.contains(player)){
            invitedPlayers.remove(player);
        }
        partyPlayers.add(player.getUniqueId());
        ChatUtil.sendMessage(player, "&eВы успешно присоединились к пати!");
        sendPartyMessage("&a" + player.getDisplayName() + " &eприсоединился к пати!");
        if (partyPlayers.size() == 1) {
            host = player;
        }
    }

    public void leave(Player player) {
        if (!partyPlayers.contains(player)) {
            ChatUtil.sendMessage(player, "&eВы не находитесь в пати!");
            return;
        }
        ChatUtil.sendMessage(player, "&eВы вышли из пати!");
        sendPartyMessage("&a" + player + " &eвышел из пати!");
        partyPlayers.remove(player);
        if (player == host) {
            sendPartyMessage("&c&lПати расформировано!");
            partyPlayers.clear();
        }
    }

    public List<UUID> getPartyPlayers() {
        return partyPlayers;
    }

    public Player getHost() {
        return host;
    }

    public List<UUID> getInvitedPlayers() {
        return invitedPlayers;
    }

}
