package net.zerotoil.dev.cyberlevels.objects;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import net.zerotoil.dev.cyberlevels.objects.leaderboard.LeaderboardPlayer;
import net.zerotoil.dev.cyberlevels.objects.levels.PlayerData;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQL {

    private final CyberLevels main;

    private final String ip, database, username, password, table;
    private final int port;
    private final boolean ssl;
    private Connection connection;

    /**
     * MySQL constructor
     *
     * @param main instance of plugin
     * @param data contains ip, port, database, username, password, table
     */
    public MySQL(CyberLevels main, String[] data, boolean ssl) {
        if (data.length < 6) throw new IllegalArgumentException();
        main.logger("&dAttempting to connect to MySQL...");
        long startTime = System.currentTimeMillis();
        this.main = main;
        ip = data[0];
        port = Integer.parseInt(data[1]);
        database = data[2];
        username = data[3];
        password = data[4];
        table = data[5];
        this.ssl = ssl;
        connect();
        if (isConnected()) main.logger("&7Connected to &eMySQL &7in &a" + (System.currentTimeMillis() - startTime) + "ms&7.", "");
    }

    // returns if connect to the database
    public boolean isConnected() {
        return connection != null;
    }

    // connect to the database
    private void connect() {
        if (isConnected()) return;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + database + "?autoReconnect=true&useSSL=" + ssl, username, password);
            makeTable();
            addTable("MAX_LEVEL", "BIGINT(20)");
        } catch (Exception e) {
            main.logger("&cThere was an issue connecting to MySQL Database.");
            throw new IllegalArgumentException();
        }
    }

    // disconnect from the database
    public void disconnect() {
        if (!isConnected()) return;
        long startTime = System.currentTimeMillis();
        main.logger("&bAttempting to disconnect from MySQL...");
        try {
            connection.close();
            connection = null;
            main.logger("&aDisconnected from MySQL successfully in &a" + (System.currentTimeMillis() - startTime) + "ms&7.", "");
        } catch (Exception e) {
            main.logger("&cThere was an issue disconnecting to MySQL Database.");
            e.printStackTrace();
        }
    }

    // creates a table in the
    private void makeTable() {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS `" + table + "` " +
                            "(`UUID` VARCHAR(36), " +
                            "`LEVEL` BIGINT(20), " +
                            "`EXP` DOUBLE(20, 10), " +
                            "`MAX_LEVEL` BIGINT(20))"
            );
            statement.executeUpdate();

        } catch (Exception e) {
            main.logger("&cFailed to create a MySQL table.");
            e.printStackTrace();
        }
    }

    public void addTable(String column) {
        addTable(column, "VARCHAR(120)");
    }

    public void addTable(String column, String type) {
        try {
            Statement statement = connection.createStatement();

            String sql = "SELECT * FROM " + table;
            ResultSet result = statement.executeQuery(sql);
            ResultSetMetaData metaData = result.getMetaData();
            int rowCount = metaData.getColumnCount();

            boolean isMyColumnPresent = false;
            for (int i = 1; i <= rowCount; i++) {
                if (column.equals(metaData.getColumnName(i))) {
                    isMyColumnPresent = true;
                }
            }

            if (!isMyColumnPresent) {
                statement.executeUpdate("ALTER TABLE " + table + " ADD " + column + " " + type + " NULL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // does a player exist in the database?
    public boolean playerInTable(Player player) {
        return playerInTable(player.getUniqueId().toString());
    }

    // does an uuid exist in the database?
    public boolean playerInTable(String uuid) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");
            statement.setString(1, uuid);
            if (statement.executeQuery().next()) return true;
        } catch (Exception e) {
            main.logger("&cFailed to check if players exists in table.");
            e.printStackTrace();
        }
        return false;
    }

    // places in player if doesnt exists and updates data
    public void updatePlayer(Player player) {

        if (!playerInTable(player)) addPlayer(player, true);

        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE " + table + " SET LEVEL=? WHERE UUID=?");
            statement.setString(1, main.levelCache().playerLevels().get(player).getLevel() + "");
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();

            statement = connection.prepareStatement("UPDATE " + table + " SET EXP=? WHERE UUID=?");
            statement.setString(1, main.levelUtils().roundDecimal(main.levelCache().playerLevels().get(player).getExp()) + "");
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();

            statement = connection.prepareStatement("UPDATE " + table + " SET MAX_LEVEL=? WHERE UUID=?");
            statement.setString(1, main.levelCache().playerLevels().get(player).getMaxLevel() + "");
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();

        } catch (Exception e) {
            main.logger("&cFailed to update player " + player.getName() + ".");
            e.printStackTrace();
        }
    }

    public PlayerData getPlayerData(Player player) {

        if (!playerInTable(player)) addPlayer(player, true);

        try {
            PlayerData playerData = new PlayerData(main, player);
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet results = statement.executeQuery();
            results.next();
            playerData.setLevel(results.getLong("LEVEL"), false);
            playerData.setExp(results.getDouble("EXP"), false, false, false);
            Long maxLevel = results.getLong("MAX_LEVEL");
            if (!(maxLevel + "").equalsIgnoreCase("null")) playerData.setMaxLevel(maxLevel);
            return playerData;

        } catch (Exception e) {
            main.logger("&cFailed to get player data for " + player.getName() + ".");
            e.printStackTrace();
            return null;
        }

    }

    public List<LeaderboardPlayer> getAllPlayers() {
        try {
            List<LeaderboardPlayer> allPlayers = new ArrayList<>();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table);
            ResultSet rs = statement.executeQuery();
            while (rs.next())
                allPlayers.add(new LeaderboardPlayer(main, rs.getString("UUID"), rs.getLong("LEVEL"), rs.getDouble("EXP")));

            return allPlayers;

        } catch (Exception e) {
            main.logger("&cFailed to generate a new leaderboard.");
            e.printStackTrace();
            return null;
        }
    }


    private void addPlayer(Player player, boolean defaultValues) {
        if (playerInTable(player)) return;

        String level = main.levelCache().startLevel() + "";
        String exp = main.levelCache().startExp() + "";
        if (!defaultValues) {
            level = main.levelCache().playerLevels().get(player).getLevel() + "";
            exp = main.levelUtils().roundDecimal(main.levelCache().playerLevels().get(player).getExp()) + "";
        }

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + table + "(UUID,LEVEL,EXP,MAX_LEVEL) VALUE (?,?,?,?)");
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, level);
            statement.setString(3, exp);
            statement.executeUpdate();

        } catch (Exception e) {
            main.logger("&cFailed to update player " + player.getName() + ".");
            e.printStackTrace();
        }

    }

}
