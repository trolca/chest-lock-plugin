package me.trololo11.chestlockplugin.managers;

import me.trololo11.chestlockplugin.LockState;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public interface DatabaseManager {

    void initialize() throws SQLException, IOException;

    void addLockState(LockState lockState) throws SQLException, IOException;

    void updateLockState(LockState lockState) throws SQLException, IOException;

    void removeLockState(LockState lockState) throws SQLException, IOException;

    ArrayList<LockState> getAllLockStates() throws SQLException, IOException;

}
