package me.trololo11.chestlockplugin;

import me.trololo11.chestlockplugin.commands.ChestLockCommand;
import me.trololo11.chestlockplugin.commands.UnlockChestCommand;
import me.trololo11.chestlockplugin.listeners.ChestLockingHandler;
import me.trololo11.chestlockplugin.managers.ChestLockingManager;
import me.trololo11.chestlockplugin.managers.DatabaseManager;
import me.trololo11.chestlockplugin.managers.MySqlManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public final class ChestLockPlugin extends JavaPlugin {

    private static ChestLockPlugin INSTANCE;

    @SuppressWarnings("FieldCanBeLocal")
    private ChestLockingManager chestLockingManager;
    private DatabaseManager databaseManager;
    public final Properties pluginsProperties = new Properties();

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onEnable() {
        ChestLockPlugin.INSTANCE = this;
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        try {
            databaseManager = new MySqlManager();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ArrayList<LockState> allLockStates;
        try {
            allLockStates = databaseManager.getAllLockStates();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

        chestLockingManager = new ChestLockingManager(allLockStates);

        setupProperties();



        getServer().getPluginManager().registerEvents(new ChestLockingHandler(chestLockingManager), this);

        getCommand("lockchest").setExecutor(new ChestLockCommand(chestLockingManager, databaseManager));
        getCommand("unlock").setExecutor(new UnlockChestCommand(chestLockingManager, databaseManager));
    }

    private void setupProperties(){
        pluginsProperties.setProperty("destroyOnExplosion", String.valueOf(getConfig().getBoolean("destroy-on-explosion")));
    }

    public static ChestLockPlugin getInstance(){
        return ChestLockPlugin.INSTANCE;
    }
}
