package me.trololo11.chestlockplugin.managers;

import me.trololo11.chestlockplugin.ChestLockPlugin;
import me.trololo11.chestlockplugin.LockState;
import me.trololo11.chestlockplugin.utils.ByteUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class MySqlManager implements DatabaseManager {

    private Connection connection = null;
    private ChestLockPlugin plugin = ChestLockPlugin.getInstance();

    public MySqlManager() throws SQLException {
        initialize();
    }

    private Connection getConnection() throws SQLException {
        if(connection != null){
            return connection;
        }

        String host = plugin.getConfig().getString("host");
        String port = plugin.getConfig().getString("port");
        String url = "jdbc:mysql://"+host+":"+port;
        String user = plugin.getConfig().getString("user");
        String password = plugin.getConfig().getString("password");
        String databaseName = plugin.getConfig().getString("database-name");

        if(databaseName == null || databaseName.trim().isEmpty()){
            databaseName = "chest_lock_database";
        }

        connection = DriverManager.getConnection(url, user, password);

        Statement statement = connection.createStatement();
        statement.execute("CREATE DATABASE IF NOT EXISTS " + databaseName);
        statement.close();
        connection.close();

        connection = DriverManager.getConnection(url + "/" + databaseName, user, password);

        return connection;
    }

    @Override
    public void initialize() throws SQLException {
        Connection connection = getConnection();

        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS lock_states(code binary(16) primary key not null, owner binary(16) not null, world1 binary(16) not null, x1 int not null, y1 int not null, z1 int, " +
                "world2 binary(16) default null, x2 int default null, y2 int default null, z2 int default null)");
        statement.execute("CREATE TABLE IF NOT EXISTS players_added(uuid binary(16) primary key not null, lock_code binary(16) not null, FOREIGN KEY (lock_code) REFERENCES lock_states(code))");

        statement.close();
    }

    @Override
    public void addLockState(LockState lockState) throws SQLException {
        Connection connection = getConnection();

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
    }

    @Override
    public void updateLockState(LockState lockState) throws SQLException {
        Connection connection = getConnection();

        PreparedStatement statement = connection.prepareStatement("UPDATE lock_states SET owner = ? WHERE code = ?");

        statement.setBytes(1, ByteUtils.getBytesFromUUID(lockState.getOwner()));
        statement.setBytes(2, lockState.getHashCode());

        statement.executeUpdate();

        statement.close();
    }

    @Override
    public void removeLockState(LockState lockState) throws SQLException {
        Connection connection = getConnection();

        PreparedStatement stateDelete = connection.prepareStatement("DELETE FROM lock_states WHERE code = ?");

        stateDelete.setBytes(1,  lockState.getHashCode());

        stateDelete.executeUpdate();

        stateDelete.close();
    }

    @Override
    public ArrayList<LockState> getAllLockStates() throws SQLException {
        ArrayList<LockState> lockStates = new ArrayList<>();
        Connection connection = getConnection();

        PreparedStatement statement = connection.prepareStatement("SELECT * FROM lock_states");

        ResultSet results = statement.executeQuery();

        while (results.next()){
            PreparedStatement playersAddedStatement;
            LockState lockState;
            UUID owner = ByteUtils.getUUIDFromBytes(results.getBytes("owner"));
            UUID world1UID = ByteUtils.getUUIDFromBytes(results.getBytes("world1"));
            UUID world2UID = ByteUtils.getUUIDFromBytes(results.getBytes("world2"));
            if(world1UID == null || owner == null) continue;
            Location location1 = new Location(Bukkit.getWorld(world1UID), results.getInt("x1"), results.getInt("y1"), results.getInt("z1"));
            Location location2 = null;
            if(world2UID != null){
                location2 = new Location(Bukkit.getWorld(world2UID), results.getInt("x2"), results.getInt("y2"), results.getInt("z2"));
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
