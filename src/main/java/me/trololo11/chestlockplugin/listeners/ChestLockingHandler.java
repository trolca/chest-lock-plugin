package me.trololo11.chestlockplugin.listeners;

import me.trololo11.chestlockplugin.ChestLockPlugin;
import me.trololo11.chestlockplugin.LockState;
import me.trololo11.chestlockplugin.managers.ChestLockingManager;
import me.trololo11.chestlockplugin.utils.PluginUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChestLockingHandler implements Listener {

    private ChestLockPlugin chestLockPlugin = ChestLockPlugin.getInstance();
    private ChestLockingManager chestLockingManager;

    public ChestLockingHandler(ChestLockingManager chestLockingManager){
        this.chestLockingManager = chestLockingManager;
    }

    @EventHandler
    public void onOpen(PlayerInteractEvent e){
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        if(block == null) return;
        if(block.getType() != Material.CHEST) return;

        LockState lockState = chestLockingManager.getLockedState(block.getLocation());
        if(lockState == null) return;
        if(lockState.getOwner().equals(player.getUniqueId())) return;

        player.sendMessage(ChatColor.RED + "This chest is locked!");
        e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if(e.getBlock().getType() != Material.CHEST) return;
        LockState lockState = chestLockingManager.getLockedState(e.getBlock().getLocation());
        Player player = e.getPlayer();
        if(lockState == null) return;
        if(lockState.getOwner().equals(player.getUniqueId())){
            player.sendMessage(ChatColor.RED + "This chest is locked by you!");
            player.sendMessage(PluginUtils.chat("&aTo modify it you have to unlock it by looking at it and typing &e/unlock"));
        }else{
            player.sendMessage(ChatColor.RED + "This chest is locked!");
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent e){
        if(e.getBlock().getType() != Material.CHEST) return;
        LockState lockState = chestLockingManager.getLockedState(e.getBlock().getLocation());
        World world = e.getBlock().getWorld();
        if(lockState == null) return;
        boolean explode = chestLockPlugin.pluginsProperties.getProperty("destroyOnExplosion").equalsIgnoreCase("true");
        if(explode){
            world.setBlockData(lockState.getOriginLocation(), Material.AIR.createBlockData());
            if(lockState.getSecondPartLocation() != null)
                world.setBlockData(lockState.getSecondPartLocation(), Material.AIR.createBlockData());
            chestLockingManager.removeLockState(lockState);
        }else{
            e.setCancelled(true);
        }

    }

}
