package me.trololo11.chestlockplugin.managers;

import me.trololo11.chestlockplugin.ChestLockPlugin;

import java.sql.*;

public class DatabaseManager {

    private Connection connection = null;
    private ChestLockPlugin plugin = ChestLockPlugin.get();

    public DatabaseManager() throws SQLException {
        initialize();
    }

    public Connection getConnection() throws SQLException {
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

    public void initialize() throws SQLException {
        Connection connection = getConnection();

        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS lock_states(code binary(16) primary key not null, owner binary(16) not null, world1 binary(16) not null, x1 int not null, y1 int not null, z1 int, " +
                "world2 binary(16) default null, x2 int default null, y2 int default null, z2 int default null)");
        statement.execute("CREATE TABLE IF NOT EXISTS players_added(uuid binary(16) primary key not null, lock_code binary(16) not null, FOREIGN KEY (lock_code) REFERENCES lock_states(code))");

        statement.close();
    }

}
