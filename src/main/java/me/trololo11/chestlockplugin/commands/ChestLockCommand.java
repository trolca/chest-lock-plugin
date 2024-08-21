package me.trololo11.chestlockplugin.commands;

import me.trololo11.chestlockplugin.LockState;
import me.trololo11.chestlockplugin.managers.ChestLockingManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChestLockCommand implements CommandExecutor {

    private final ChestLockingManager chestLockingManager;

    public ChestLockCommand(ChestLockingManager chestLockingManager){
        this.chestLockingManager = chestLockingManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(sender instanceof Player player)) return true;
        Block block = player.getTargetBlockExact(5);
        if(block == null || block.getType() != Material.CHEST){
            player.sendMessage(ChatColor.RED + "Please look at the chest you want to lock!");
            return true;
        }
        Chest chest = (Chest) block.getState();
        LockState lockState = getLockState(player, chest);
        chestLockingManager.addLockState(lockState);

        player.sendMessage(ChatColor.GREEN + "Successfully locked this chest!");

        return true;
    }

    private static @NotNull LockState getLockState(Player player, Chest chest) {
        Location secondPartLocation = null;
        Location originLocation;
        if(chest.getInventory().getHolder() instanceof DoubleChest doubleChest){
            Location doubleInvLoc = doubleChest.getLocation();
            boolean isZ = Math.abs(doubleInvLoc.getZ() - ( (int) doubleInvLoc.getZ() )) > 0;

            doubleInvLoc.add(isZ ? 0 : 0.5, 0, isZ ? 0.5 : 0);
            originLocation = doubleInvLoc.clone();
            doubleInvLoc.subtract(isZ ? 0 : 1, 0, isZ ? 1 : 0);
            secondPartLocation = doubleInvLoc.clone();
        }else{
            originLocation = chest.getLocation();
        }

        return new LockState(player.getUniqueId(), originLocation, secondPartLocation, new ArrayList<>());
    }
}
