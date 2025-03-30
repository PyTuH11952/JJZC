package Party;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PartyList {
        private static List<Party> partyes = new ArrayList<>();

        public static boolean hasParty(Player player) {
            for (Party party : partyes) {
                if (party.getPartyPlayers().contains(player)) {
                    return true;
                }
            }
            return false;
        }

        public static Party getParty(Player player){
            for (Party party : partyes){
                if (party.getPartyPlayers().contains(player)){
                    return  party;
                }
            }
            return null;
        }
        public static boolean hasInvited(Player player){
            for (Party party : partyes){
                if(party.getInvitedPlayers().contains(player)){
                    return true;
                }
            }
            return false;
        }

        public static Party getInvitedParty(Player player){
            for(Party party : partyes){
                if(party.getInvitedPlayers().contains(player)){
                    return party;
                }
            }
            return null;
        }
    }
