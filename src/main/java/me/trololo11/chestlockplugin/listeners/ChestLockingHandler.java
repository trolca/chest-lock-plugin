package me.trololo11.chestlockplugin.listeners;

import me.trololo11.chestlockplugin.ChestLockPlugin;
import me.trololo11.chestlockplugin.LockState;
import me.trololo11.chestlockplugin.repositories.LockStatesRepository;
import me.trololo11.chestlockplugin.utils.PluginUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ChestLockingHandler implements Listener {

    private ChestLockPlugin chestLockPlugin = ChestLockPlugin.get();
    private LockStatesRepository lockStatesRepository;

    public ChestLockingHandler(LockStatesRepository lockStatesRepository){
        this.lockStatesRepository = lockStatesRepository;
    }

    @EventHandler
    public void onOpen(PlayerInteractEvent e){
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        if(block == null) return;
        if(block.getType() != Material.CHEST) return;

        LockState lockState = lockStatesRepository.getLockState(block.getLocation());
        if(lockState == null) return;
        if(lockState.getOwner().equals(player.getUniqueId())) return;

        player.sendMessage(ChatColor.RED + "This chest is locked!");
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if(e.getBlock().getType() != Material.CHEST) return;
        Bukkit.getScheduler().runTaskLater(chestLockPlugin, () -> {
            Chest chest = (Chest) e.getBlock().getState();
            LockState lockState = null;
            if(chest.getInventory().getHolder() instanceof DoubleChest doubleChest){
                Location[] locations = PluginUtils.getBlockLocationsDoubleChest(doubleChest);
                for(Location location : locations){
                    lockState = lockStatesRepository.getLockState(location);
                    if(lockState != null) break;
                }

                if(lockState == null) return;
            }else{
                return;
            }

            Player player = e.getPlayer();
            if(lockState.getOwner().equals(player.getUniqueId())){
                player.sendMessage(ChatColor.RED + "This chest is locked by you!");
                player.sendMessage(PluginUtils.chat("&aTo modify it you have to unlock it using &e/unlock"));
            }else{
                player.sendMessage(ChatColor.RED + "This chest is locked!");
            }
            if(chest.getLocation().getWorld() == null) return;
            chest.getLocation().getWorld().getBlockAt(chest.getLocation()).setType(Material.AIR);
            if(player.getGameMode() == GameMode.SURVIVAL) {
                player.getInventory().addItem(new ItemStack(Material.CHEST));
            }
        }, 1L);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if(e.getBlock().getType() != Material.CHEST) return;
        LockState lockState = lockStatesRepository.getLockState(e.getBlock().getLocation());
        if(lockState == null) return;
        Player player = e.getPlayer();
        if(lockState.getOwner().equals(player.getUniqueId())){
            player.sendMessage(ChatColor.RED + "This chest is locked by you!");
            player.sendMessage(PluginUtils.chat("&aTo modify it you have to unlock it using &e/unlock"));
        }else{
            player.sendMessage(ChatColor.RED + "This chest is locked!");
        }
        e.setCancelled(true);
    }


}
