package me.trololo11.chestlockplugin.listeners;

import me.trololo11.chestlockplugin.utils.Menu;
import me.trololo11.chestlockplugin.utils.PluginUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MenuHandler implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e){

        if(e.getInventory().getHolder() instanceof Menu menu){
            ItemStack itemStack = e.getCurrentItem();
            if(itemStack == null) return;
            if(!PluginUtils.hasPrivateName(itemStack)) return;
            menu.handleMenu(e);
        }

    }
}
