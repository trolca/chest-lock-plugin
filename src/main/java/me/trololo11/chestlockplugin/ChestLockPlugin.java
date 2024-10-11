package me.trololo11.chestlockplugin;

import me.trololo11.chestlockplugin.commands.ChestLockCommand;
import me.trololo11.chestlockplugin.commands.UnlockChestCommand;
import me.trololo11.chestlockplugin.listeners.ChestLockingHandler;
import me.trololo11.chestlockplugin.listeners.MenuHandler;
import me.trololo11.chestlockplugin.repositories.implementations.ChestLockingSqlManager;
import me.trololo11.chestlockplugin.managers.DatabaseManager;
import me.trololo11.chestlockplugin.repositories.LockStatesRepository;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Properties;

public final class ChestLockPlugin extends JavaPlugin {

    private static ChestLockPlugin INSTANCE;
    private static NamespacedKey PRIVATE_KEY;

    @SuppressWarnings("FieldCanBeLocal")
    private LockStatesRepository lockStatesRepository;
    private DatabaseManager databaseManager;
    public final Properties pluginsProperties = new Properties();

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onEnable() {
        ChestLockPlugin.INSTANCE = this;
        PRIVATE_KEY = new NamespacedKey(ChestLockPlugin.INSTANCE, "chest_lock");
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        try {
            databaseManager = new DatabaseManager();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            lockStatesRepository = new ChestLockingSqlManager();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        setupProperties();

        getServer().getPluginManager().registerEvents(new ChestLockingHandler(lockStatesRepository), this);
        getServer().getPluginManager().registerEvents(new MenuHandler(), this);

        getCommand("lockchest").setExecutor(new ChestLockCommand(lockStatesRepository));
        getCommand("unlock").setExecutor(new UnlockChestCommand(lockStatesRepository));
    }

    private void setupProperties(){
        pluginsProperties.setProperty("destroyOnExplosion", String.valueOf(getConfig().getBoolean("destroy-on-explosion")));
    }

    public DatabaseManager getDatabaseManager(){
        return databaseManager;
    }

    public static ChestLockPlugin get(){
        return ChestLockPlugin.INSTANCE;
    }

    public static NamespacedKey getPrivateKey(){
        return PRIVATE_KEY;
    }
}
