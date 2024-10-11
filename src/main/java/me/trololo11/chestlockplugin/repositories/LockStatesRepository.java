package me.trololo11.chestlockplugin.repositories;

import me.trololo11.chestlockplugin.LockState;
import org.bukkit.Location;

import java.io.IOException;
import java.sql.SQLException;

public interface LockStatesRepository {

    LockState getLockState(Location chestLocation);

    void addLockState(LockState state) throws SQLException, IOException;

    void removeLockState(LockState state) throws SQLException, IOException;

}
