package me.trololo11.chestlockplugin.repositories.implementations;

import me.trololo11.chestlockplugin.ChestLockPlugin;
import me.trololo11.chestlockplugin.LockState;
import me.trololo11.chestlockplugin.managers.DatabaseManager;
import me.trololo11.chestlockplugin.repositories.LockStatesRepository;
import me.trololo11.chestlockplugin.utils.ByteUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ChestLockingSqlManager implements LockStatesRepository {

    private List<LockState> lockStates;
    private final DatabaseManager databaseManager = ChestLockPlugin.get().getDatabaseManager();

    public ChestLockingSqlManager() throws SQLException {
        Connection connection = databaseManager.getConnection();

        this.lockStates = getAllLockStates(connection);
    }

    @Nullable
    @Override
    public LockState getLockState(Location chestLocation) {
        for(LockState lockState : lockStates){
            if(lockState.isChestLocked(chestLocation))
                return lockState;
        }
        return null;
    }

    @Override
    public void addLockState(LockState lockState) throws SQLException {
        Connection connection = databaseManager.getConnection();

        PreparedStatement statement = connection.prepareStatement("INSERT INTO lock_states VALUES (? ,? ,? ,?, ?, ?, ?, ?, ?, ?)");
        statement.setBytes(1, lockState.getHashCode());
        statement.setBytes(2, ByteUtils.getBytesFromUUID(lockState.getOwner()));

        Location location1 = lockState.getOriginLocation();
        Location location2 = lockState.getSecondPartLocation();

        statement.setBytes(3, ByteUtils.getBytesFromUUID(Objects.requireNonNull(location1.getWorld()).getUID()));
        statement.setInt(4, (int) location1.getX());
        statement.setInt(5, (int) location1.getY());
        statement.setInt(6, (int) location1.getZ());

        if(location2 != null){
            statement.setBytes(7, ByteUtils.getBytesFromUUID(Objects.requireNonNull(location2.getWorld()).getUID()));
            statement.setInt(8, (int) location2.getX());
            statement.setInt(9, (int) location2.getY());
            statement.setInt(10, (int) location2.getZ());
        }else{
            statement.setNull(7, Types.BINARY);
            statement.setNull(8, Types.INTEGER);
            statement.setNull(9, Types.INTEGER);
            statement.setNull(10, Types.INTEGER);
        }

        statement.executeUpdate();

        statement.close();
        lockStates.add(lockState);
    }

    @Override
    public void removeLockState(LockState lockState) throws SQLException {
        Connection connection = databaseManager.getConnection();

        PreparedStatement stateDelete = connection.prepareStatement("DELETE FROM lock_states WHERE code = ?");

        stateDelete.setBytes(1,  lockState.getHashCode());

        stateDelete.executeUpdate();

        stateDelete.close();
        lockStates.remove(lockState);
    }

    private List<LockState> getAllLockStates(Connection connection) throws SQLException {
        ArrayList<LockState> lockStates = new ArrayList<>();

        PreparedStatement statement = connection.prepareStatement("SELECT * FROM lock_states");

        ResultSet results = statement.executeQuery();

        while (results.next()){
            PreparedStatement playersAddedStatement;
            LockState lockState;
            UUID owner = ByteUtils.getUUIDFromBytes(results.getBytes("owner"));
            UUID world1UID = ByteUtils.getUUIDFromBytes(results.getBytes("world1"));
            UUID world2UID = ByteUtils.getUUIDFromBytes(results.getBytes("world2"));
            if(world1UID == null || owner == null) continue;
            org.bukkit.Location location1 = new org.bukkit.Location(Bukkit.getWorld(world1UID), results.getInt("x1"), results.getInt("y1"), results.getInt("z1"));
            org.bukkit.Location location2 = null;
            if(world2UID != null){
                location2 = new org.bukkit.Location(Bukkit.getWorld(world2UID), results.getInt("x2"), results.getInt("y2"), results.getInt("z2"));
            }

            playersAddedStatement = connection.prepareStatement("SELECT uuid FROM players_added WHERE lock_code = ?");
            playersAddedStatement.setBytes(1, results.getBytes("code"));

            ResultSet addedResults = playersAddedStatement.executeQuery();
            ArrayList<UUID> addedPlayers = new ArrayList<>();
            while (addedResults.next()){
                addedPlayers.add(ByteUtils.getUUIDFromBytes(addedResults.getBytes("uuid")));
            }

            lockState = new LockState(owner, location1, location2, addedPlayers);
            playersAddedStatement.close();
            addedResults.close();
            lockStates.add(lockState);
        }

        results.close();
        statement.close();

        return lockStates;
    }

}
