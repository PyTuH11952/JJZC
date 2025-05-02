package JustJoyDEV.Party.Commands;

import Arena.ArenaList;
import Party.*;
import Utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(commandSender instanceof Player))
            return true;
        Player player = (Player) commandSender;
        if (args.length == 0) {
            ChatUtil.sendMessage(player, "&aДоступные команды:");
            ChatUtil.sendMessage(player, "&e/party <ник> &a- пригласить игрока в пати");
            ChatUtil.sendMessage(player, "&e/party accept &a- принять запрос в пати");
            ChatUtil.sendMessage(player, "&e/party cancel &a- отклонить запрос в пати");
            ChatUtil.sendMessage(player, "&e/party kick <ник> &a- кикнуть игрока из пати");
            ChatUtil.sendMessage(player, "&e/party warp &a- телепортировать всех участников пати к себе");
            ChatUtil.sendMessage(player, "&e/partychat <сообщение> &a- написать сообщение в чат пати");
            return true;
        }
        if (args.length == 1){
            if (args[0].equals("accept") || args[0].equals("a")){
                if (PartyList.hasInvited(player)){
                    PartyList.getInvitedParty(player).join(player);
                } else {
                    ChatUtil.sendMessage(player, "&cУ вас нет активных приглашений в пати!");
                }
                return true;
            }
            if (args[0].equals("cancel") || args[0].equals("c")){
                if (PartyList.hasInvited(player)){
                    PartyList.getInvitedParty(player).getInvitedPlayers().remove(player);
                } else{
                    ChatUtil.sendMessage(player, "&cУ вас нет активных приглашений в пати!");
                }
                return true;
            }
            if (args[0].equals("warp") || args[0].equals("w")){
                if (PartyList.hasParty(player)){
                    if(PartyList.getParty(player).getHost() == player) {
                        ChatUtil.sendMessage(player, "&eВсе учестники пати перемещены к вам!");
                        if (ArenaList.hasArena(player)) {
                            for (Player partyPlayer : PartyList.getParty(player).getPartyPlayers()) {
                                if (partyPlayer != PartyList.getParty(player).getHost()) {
                                    ArenaList.get(player).join(partyPlayer);
                                }
                            }
                        } else {
                            for (Player partyPlayer : PartyList.getParty(player).getPartyPlayers()) {
                                if (partyPlayer != PartyList.getParty(player).getHost() && ArenaList.hasArena((partyPlayer))) {
                                    ArenaList.get(partyPlayer).leave(partyPlayer);
                                }
                            }
                        }
                    } else {
                        ChatUtil.sendMessage(player, "&cДанная команда доступна только создателю пати!");
                    }
                    } else {
                    ChatUtil.sendMessage(player, "&cВы не состоите в пати!");
                }
                return true;
            }
            if (Bukkit.getPlayer(args[0]) != null) {
                if (!PartyList.hasParty(Bukkit.getPlayer(args[0]))) {
                    ChatUtil.sendMessage(player, "&aОтправлено приглашение в пати игроку &e" + player.getDisplayName());
                    if (PartyList.hasParty(player)) {
                        if (!PartyList.getParty(player).getInvitedPlayers().contains(Bukkit.getPlayer(args[0]))) {
                            PartyList.getParty(player).invite(Bukkit.getPlayer(args[0]), player);
                        } else {
                            ChatUtil.sendMessage(player, "&cЭтот игрок уже приглашен в пати!");
                        }
                    } else {
                        Party party = new Party();
                        PartyList.getPartyes().add(party);
                        party.join(player);
                        party.invite(Bukkit.getPlayer(args[0]), player);
                    }
                } else {
                    ChatUtil.sendMessage(player, "&cИгрок " + args[0] + " уже состоит в пати!");
                }
            } else {
                ChatUtil.sendMessage(player, "&cИгрок " + args[0] + " не в сети!");
            }
        }
        if (args[0].equals("kick") || args[0].equals("k")){
            if(PartyList.hasParty(player)){
                if(PartyList.getParty(player).getHost() == player) {
                    if (Bukkit.getPlayer(args[1]) != null) {
                        if (PartyList.getParty(Bukkit.getPlayer(args[1])) == PartyList.getParty(player)) {
                            PartyList.getParty(player).leave(Bukkit.getPlayer(args[1]));
                        } else {
                            ChatUtil.sendMessage(player, "&cИгрок " + args[1] + " не состоит в этом пати!");
                        }
                    } else {
                        ChatUtil.sendMessage(player, "&cИгрок " + args[1] + " не в сети!");
                    }
                } else {
                    ChatUtil.sendMessage(player, "&cДанная команда доступна только создателю пати!");
                }
            }
        }
        return true;
    }
}
