package me.trololo11.chestlockplugin;

import me.trololo11.chestlockplugin.commands.ChestLockCommand;
import me.trololo11.chestlockplugin.commands.UnlockChestCommand;
import me.trololo11.chestlockplugin.listeners.ChestLockingHandler;
import me.trololo11.chestlockplugin.managers.ChestLockingManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Properties;

public final class ChestLockPlugin extends JavaPlugin {

    private static ChestLockPlugin INSTANCE;

    @SuppressWarnings("FieldCanBeLocal")
    private ChestLockingManager chestLockingManager;
    public final Properties pluginsProperties = new Properties();

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        chestLockingManager = new ChestLockingManager();
        ChestLockPlugin.INSTANCE = this;
        setupProperties();

        getServer().getPluginManager().registerEvents(new ChestLockingHandler(chestLockingManager), this);

        getCommand("lockchest").setExecutor(new ChestLockCommand(chestLockingManager));
        getCommand("unlock").setExecutor(new UnlockChestCommand(chestLockingManager));
    }

    private void setupProperties(){
        pluginsProperties.setProperty("destroyOnExplosion", String.valueOf(getConfig().getBoolean("destroy-on-explosion")));
    }

    public static ChestLockPlugin getInstance(){
        return ChestLockPlugin.INSTANCE;
    }
}
