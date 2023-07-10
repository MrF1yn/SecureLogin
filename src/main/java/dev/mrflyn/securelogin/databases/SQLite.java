package dev.mrflyn.securelogin.databases;

import dev.mrflyn.securelogin.LoginData;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;

import static dev.mrflyn.securelogin.SecureLogin.LOGGER;

public class SQLite implements IDatabase {
    private String url;
    private Connection connection;

    @Override
    public String name() {
        return "SQLite";
    }

    @Override
    public boolean connect() {
        File folder = new File("config/secure_login");
        LOGGER.info("Initializing SQLite!");
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                LOGGER.error("Could not create secure_login directory!");
            }
        }
        File dataFolder = new File(folder.getPath() + "/data.db");
        if (!dataFolder.exists()) {
            try {
                if (!dataFolder.createNewFile()) {
                    LOGGER.error("Could not create data.db file!");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        this.url = "jdbc:sqlite:" + dataFolder;
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException e) {
            if (e instanceof ClassNotFoundException) {
                LOGGER.error("Could not find SQLite driver!");
            }
            e.printStackTrace();
            return false;
        }
        LOGGER.info("SQLite connection successful!");
        return true;
    }

    @Override
    public void init() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS login_data (uuid VARCHAR(200) PRIMARY KEY, username VARCHAR(200), password_hash TEXT, ip_address VARCHAR(200));";
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
                LOGGER.info("SQLite Tables Loaded!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean isPresent(UUID uuid) {
        String sql = "SELECT uuid FROM login_data WHERE uuid=?;";
        try {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                ResultSet result = statement.executeQuery();
                return result.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isPresent(String name) {
        String sql = "SELECT username FROM login_data WHERE username=?;";
        try {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                ResultSet result = statement.executeQuery();
                return result.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setData(LoginData data) {
//        INSERT OR IGNORE INTO my_table (name, age) VALUES ('Karen', 34)
//        UPDATE my_table SET age = 34 WHERE name='Karen'
        String sql1 = "INSERT OR IGNORE INTO login_data (uuid, username, password_hash, ip_address) VALUES (?, ?, ?, ?);";
        String sql2 = "UPDATE login_data SET username=?, password_hash=?, ip_address=? WHERE uuid=?;";
        try {
            try (PreparedStatement statement = connection.prepareStatement(sql1)) {
                statement.setString(1, data.getUuid().toString());
                statement.setString(2, data.getUserName());
                statement.setString(3, data.getPassword());
                statement.setString(4, data.getIp());
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement(sql2)) {
                statement.setString(4, data.getUuid().toString());
                statement.setString(1, data.getUserName());
                statement.setString(2, data.getPassword());
                statement.setString(3, data.getIp());
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public LoginData getData(UUID uuid) {
        String sql = "SELECT * FROM login_data WHERE uuid=?;";
        try {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                ResultSet result = statement.executeQuery();
                if (!result.next()) return null;
                return new LoginData(UUID.fromString(result.getString("uuid")), result.getString("username"),
                        result.getString("password_hash"), result.getString("ip_address"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public LoginData getData(String name) {
        String sql = "SELECT * FROM login_data WHERE username=?;";
        try {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                ResultSet result = statement.executeQuery();
                if (!result.next()) return null;
                return new LoginData(UUID.fromString(result.getString("uuid")), result.getString("username"),
                        result.getString("password_hash"), result.getString("ip_address"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteData(UUID uuid) {
        String sql = "DELETE FROM login_data WHERE uuid=?;";
        try  {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                statement.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteData(String name) {
        String sql = "DELETE FROM login_data WHERE username=?;";
        try {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                statement.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
