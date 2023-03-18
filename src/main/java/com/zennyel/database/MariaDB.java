package com.zennyel.database;

import com.zennyel.QuizPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MariaDB {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private String table;
    private String url;

    private Connection connection;

    public MariaDB(FileConfiguration config) {
        this.host = config.getString("MariaDB.host");
        this.port = config.getInt("MariaDB.port");
        this.database = config.getString("MariaDB.database");
        this.username = config.getString("MariaDB.username");
        this.password = config.getString("MariaDB.password");
        this.table = config.getString("MariaDB.table");
        url = "jdbc:mariadb://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false";
    }


    public void connect() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
            System.out.println("Sucefully connected!");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("§4Connection failed, plugin shutdown!");
            System.out.println("§4Please check the connection data!");
            Bukkit.getPluginManager().disablePlugin(QuizPlugin.getPlugin(QuizPlugin.class));
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void CreateTable(){
        String sql = "CREATE TABLE IF NOT EXISTS player_quiz(id VARCHAR(36) NOT NULL, points INT NOT NULL DEFAULT 0, PRIMARY KEY (id));";
        try{
            Statement stm = connection.createStatement();
            stm.execute(sql);
            stm.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertPoints(Player player, int points){
        String sql = "INSERT INTO player_quiz (id, points) VALUES (?,?)";
        String id = player.getUniqueId().toString();
        try{
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, id);
            stm.setInt(2, points);
            stm.executeUpdate();
            stm.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePoints(Player player, int points) {
        String sql = "UPDATE player_quiz SET points = ? WHERE id = ?";
        String id = player.getUniqueId().toString();
        try {
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, points);
            stm.setString(2, id);
            stm.executeUpdate();
            stm.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public int getPoints(Player player) {
        String id = player.getUniqueId().toString();
        String sql = "SELECT points FROM player_quiz WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int points = rs.getInt("points");
                return points;
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Map<String, Integer> getPlayerPoints() {
        String sql = "SELECT id, points FROM player_quiz";
        Map<String, Integer> playerPoints = new HashMap<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String playerId = rs.getString("id");
                int points = rs.getInt("points");
                Player player = Bukkit.getPlayer(UUID.fromString(playerId));
                if (player != null) {
                    playerPoints.put(player.getName(), points);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerPoints;
    }

    public void displayTopTenPlayers(Player player) {
        String sql = "SELECT * FROM player_quiz ORDER BY points DESC LIMIT 10";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            player.sendMessage("§5§l[QuizEvent] §fBest Players ↓");;
            int i = 0;

            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString("id"));
                String playerName = Bukkit.getPlayer(id).getName();
                int playerPoints = rs.getInt("points");
                System.out.println(playerName + ", " + playerPoints);
                i = i + 1;
                player.sendMessage("                                           ");
                player.sendMessage("§5§l[QuizEvent] §6" + i + "ª §f" + playerName + ", §5" + playerPoints);
                Bukkit.broadcastMessage("                   ");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
