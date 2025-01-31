package Commands;

import Arena.Arena;
import Arena.ArenaList;
import Utils.ChatUtil;
import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import Arena.Game;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.Random;


public class Test2CMD implements CommandExecutor {

    private void additems(Location location, ItemStack item) {
        Block block = location.getBlock();
        Inventory inv;
        Random random = new Random();
        if (block.getType() == Material.CHEST){
            Chest chest = (Chest) block.getState();
            inv = chest.getInventory();
            inv.setItem(random.nextInt(26), item);
        } else if (block.getType() == Material.BARREL){
            Barrel barrel = (Barrel) block.getState();
            inv = barrel.getInventory();
            inv.setItem(random.nextInt(26), item);
        }

    }
    private ItemStack getitem (String itemid, int count) {
        ItemStack item = null;
        Optional<ExecutableItemInterface> eiOpt = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(itemid);
        if (eiOpt.isPresent()) {
            item = eiOpt.get().buildItem(count, Optional.empty());
        }
        return item;
    }

    @Override

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Random random = new Random();
        Location location = new Location(Bukkit.getWorld("world"), 0, 2, 0);
        int locationnumber = 1;
        int armorweaponcount = random.nextInt(3);
        int materialcount = random.nextInt(3);
        int differentcount = random.nextInt(2);
        int locitemcount = random.nextInt(2);

        Block block = location.getBlock();
        Inventory inv;
        if (block.getType() == Material.CHEST){
            Chest chest = (Chest) block.getState();
            inv = chest.getInventory();
            inv.clear();
        } else if (block.getType() == Material.BARREL){
            Barrel barrel = (Barrel) block.getState();
            inv = barrel.getInventory();
            inv.clear();
        }

        for (int i = 0; i <= armorweaponcount; i++) {
            double d = Math.random();

            if (d < 0.39) {
                int tir1armor = random.nextInt(11) + 1;
                additems(location, getitem("tir1armor" + tir1armor, 1));
            } else if (d < 0.78) {
                int tir1armor = random.nextInt(9) + 1;
                additems(location, getitem("tir1weapon" + tir1armor, 1));
            } else if (d < 0.83) {
                int tir2armor = random.nextInt(7) + 1;
                additems(location, getitem("tir2armor" + tir2armor, 1));
            } else if (d < 0.88) {
                int tir2weapon = random.nextInt(3) + 1;
                additems(location, getitem("tir2weapon" + tir2weapon, 1));
            } else if (d < 0.91) {
                int tir3armor = random.nextInt(7) + 1;
                additems(location, getitem("tir3armor" + tir3armor, 1));
            } else if (d < 0.94) {
                int tir3weapon = random.nextInt(2) + 1;
                additems(location, getitem("tir3weapon" + tir3weapon, 1));
            } else if (d < 0.96) {
                int tir4armor = random.nextInt(7) + 1;
                additems(location, getitem("tir4armor" + tir4armor, 1));
            } else if (d < 0.98) {
                int tir4weapon = random.nextInt(2) + 1;
                additems(location, getitem("tir4weapon" + tir4weapon, 1));
            } else if (d < 0.99) {
                int tir5armor = random.nextInt(7) + 1;
                additems(location, getitem("tir5armor" + tir5armor, 1));
            } else {
                int tir5weapon = random.nextInt(2) + 1;
                additems(location, getitem("tir5weapon" + tir5weapon, 1));
            }
        }
            for (int i = 0; i <= materialcount; i++) {
                double d = Math.random();
                int matcount = random.nextInt(2) + 1;
                if (d < 0.5) {
                    additems(location, getitem("material1", matcount));
                } else if (d < 0.75) {
                    additems(location, getitem("material2", matcount));
                } else if (d < 0.9) {
                    additems(location, getitem("material3", matcount));
                } else if (d < 0.97) {
                    additems(location, getitem("material4", matcount));
                } else {
                    additems(location, getitem("material5", matcount));
                }
            }
            for (int i = 0; i <= differentcount; i++){
                double d = Math.random();
                int diffcount = random.nextInt(2) + 1;
                if (d < 0.7){
                    int eda = random.nextInt(4) + 1;
                    additems(location, getitem("eda" + eda, diffcount));
                } else if (d < 0.77){
                    int bomba = random.nextInt(4) + 1;
                    additems(location, getitem("bomba" + bomba, 1));
                } else if (d < 0.84){
                    additems(location, getitem("repair", 1));
                } else if (d < 0.91){
                    additems(location, getitem("hpregen", 1));
                } else if (d < 0.97){
                    additems(location, getitem("dopitem2", 1));
                } else if (d < 0.98){
                    additems(location, getitem("dopitem1", 1));
                } else if (d < 0.99){
                    additems(location, getitem("lom", 1));
                } else {
                    additems(location, getitem("lom2", 1));
                }
            }
        for (int i = 0; i <= locitemcount; i++){
            double d = Math.random();
            int loccount = random.nextInt(2) + 1;
            int lootcount = random.nextInt(3) + 1;
            if (d < 0.02){
                additems(location, getitem("loc"+locationnumber+"loot"+lootcount, loccount));
            }


        }

        return  true;
    }
}
