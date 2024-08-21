package me.trololo11.chestlockplugin.commands;

import me.trololo11.chestlockplugin.LockState;
import me.trololo11.chestlockplugin.managers.ChestLockingManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UnlockChestCommand implements CommandExecutor {

    private ChestLockingManager chestLockingManager;

    public UnlockChestCommand(ChestLockingManager chestLockingManager){
        this.chestLockingManager = chestLockingManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) return true;

        Block block = player.getTargetBlockExact(6);
        if(block == null || block.getType() != Material.CHEST) {
            player.sendMessage(ChatColor.RED + "You have to look at a chest!");
            return true;
        }
        LockState lockState = chestLockingManager.getLockedState(block.getLocation());
        if(lockState == null){
            player.sendMessage(ChatColor.RED + "This chest isn't locked!");
            return true;
        }

        if(!lockState.getOwner().equals(player.getUniqueId())){
            player.sendMessage(ChatColor.RED + "This chest isn't owned by you!");
            return true;
        }

        chestLockingManager.removeLockState(lockState);
        player.sendMessage(ChatColor.GREEN + "Successfully removed lock on this chest!");

        return true;
    }
}
