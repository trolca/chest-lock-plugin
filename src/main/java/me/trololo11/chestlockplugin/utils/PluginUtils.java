package me.trololo11.chestlockplugin.utils;

import me.trololo11.chestlockplugin.ChestLockPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PluginUtils {

    public static String chat(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    /**
     * Gets both the locations of both of the blocks of the specified double chest.
     * @param doubleChest The double chest to get the location for.
     * @return The block locations of the double chest.
     */
    public static Location[] getBlockLocationsDoubleChest(DoubleChest doubleChest){
        Location[] locations = new Location[2];

        Location doubleInvLoc = doubleChest.getLocation();
        boolean isZ = Math.abs(doubleInvLoc.getZ() - ( (int) doubleInvLoc.getZ() )) > 0;

        doubleInvLoc.add(isZ ? 0 : 0.5, 0, isZ ? 0.5 : 0);
        locations[0] = doubleInvLoc.clone();
        doubleInvLoc.subtract(isZ ? 0 : 1, 0, isZ ? 1 : 0);
        locations[1] = doubleInvLoc.clone();

        return locations;
    }

    public static boolean hasPrivateName(@NotNull ItemStack itemStack){
        return getPrivateName(itemStack) != null;
    }

    /**
     * Checks if the private name of the item is equal to the specified string. (Case insensitive)
     * @param item The item to check the private name.
     * @param string The string to check to.
     * @return If the private name is equal
     * @see ChestLockPlugin#getPrivateKey()
     */
    public static boolean isPrivateNameEqual(@NotNull ItemStack item, String string){
        String privateName = getPrivateName(item);
        if(privateName == null) return false;

        return privateName.equalsIgnoreCase(string);
    }

    /**
     * Gets the private name from the item.
     * @param item The item to get the name from.
     * @return The private name of the item.
     */
    public static String getPrivateName(@NotNull ItemStack item){
        if(item.getItemMeta() == null) return null;

        return getPrivateName(item.getItemMeta());
    }

    public static void setPrivateName(@NotNull ItemMeta itemMeta, String privateName){
        itemMeta.getPersistentDataContainer().set(ChestLockPlugin.getPrivateKey(), PersistentDataType.STRING, privateName);
    }

    /**
     * Gets the private name from the item meta.
     * @param itemMeta The item meta to get the name from.
     * @return The private name of the item meta.
     */
    public static String getPrivateName(@NotNull ItemMeta itemMeta){
        return itemMeta.getPersistentDataContainer().get(ChestLockPlugin.getPrivateKey(), PersistentDataType.STRING);
    }

}
