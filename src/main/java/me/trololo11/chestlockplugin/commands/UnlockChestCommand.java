package me.trololo11.chestlockplugin.commands;

import me.trololo11.chestlockplugin.ChestLockPlugin;
import me.trololo11.chestlockplugin.LockState;
import me.trololo11.chestlockplugin.repositories.LockStatesRepository;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;

public class UnlockChestCommand implements CommandExecutor {

    private LockStatesRepository lockStatesRepository;


    public UnlockChestCommand(LockStatesRepository lockStatesRepository){
        this.lockStatesRepository = lockStatesRepository;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) return true;

        Block block = player.getTargetBlockExact(6);
        if(block == null || block.getType() != Material.CHEST) {
            player.sendMessage(ChatColor.RED + "You have to look at a chest!");
            return true;
        }
        LockState lockState = lockStatesRepository.getLockState(block.getLocation());
        if(lockState == null){
            player.sendMessage(ChatColor.RED + "This chest isn't locked!");
            return true;
        }

        if(!lockState.getOwner().equals(player.getUniqueId())){
            player.sendMessage(ChatColor.RED + "This chest isn't owned by you!");
            return true;
        }

        try {
            lockStatesRepository.removeLockState(lockState);
        } catch (SQLException | IOException e) {
            ChestLockPlugin.get().getLogger().severe("Error while removing lock state from database");
            return true;
        }

        player.sendMessage(ChatColor.GREEN + "Successfully removed lock on this chest!");

        return true;
    }
}
