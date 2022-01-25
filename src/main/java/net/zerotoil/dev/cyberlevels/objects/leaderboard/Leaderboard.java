package net.zerotoil.dev.cyberlevels.objects.leaderboard;

import net.zerotoil.dev.cyberlevels.CyberLevels;
import net.zerotoil.dev.cyberlevels.objects.levels.LevelObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// has to be loaded after MySQL/flatfile
public class Leaderboard {

    private final CyberLevels main;
    private List<LeaderboardPlayer> topTenPlayers;
    final private List<LeaderboardPlayer> loadingList = new ArrayList<>();

    private boolean updating = false;

    public Leaderboard(CyberLevels main) {
        this.main = main;

        for (int i = 0; i < 10; i++) loadingList.add(i, new LeaderboardPlayer(main, null, 0, 0));
        topTenPlayers = loadingList;
        updateLeaderboard();

    }


    public void updateLeaderboard() {
        topTenPlayers = loadingList;
        updating = true;

        List<LeaderboardPlayer> allPlayers;
        if (main.levelCache().getMySQL() != null && main.levelCache().getMySQL().isConnected()) {
            allPlayers = main.levelCache().getMySQL().getAllPlayers();
        } else {
            allPlayers = getFlatFileLeaderboard();
        }
        if (allPlayers != null)
            topTenPlayers = generateLeaderboard(allPlayers);

        updating = false;
    }

    private List<LeaderboardPlayer> getFlatFileLeaderboard() {
        File file = new File(main.getDataFolder() + File.separator + "player_data");
        if (!file.exists()) return null;
        if (!file.isDirectory()) return null;
        List<LeaderboardPlayer> allPlayers = new ArrayList<>();

        for (File f : file.listFiles()) {
            try {
                String uuid = f.getName().replace(".clv", "");
                System.out.println(uuid);
                Scanner scanner = new Scanner(f);
                long level = Long.parseLong(scanner.nextLine());
                double exp = Double.parseDouble(scanner.nextLine());
                allPlayers.add(new LeaderboardPlayer(main, uuid, level, exp));
            } catch (Exception e) {
                // nothings
            }
        }

        return allPlayers;
    }

    private List<LeaderboardPlayer> generateLeaderboard(List<LeaderboardPlayer> allPlayers) {
        List<LeaderboardPlayer> topPlayers = new ArrayList<>();
        for (int i = 0; i < 10; i++) topPlayers.add(new LeaderboardPlayer(main, null, main.levelCache().startLevel(), main.levelCache().startExp()));

        for (LeaderboardPlayer player : allPlayers) {

            for (int i = 9; i >= 0; i--) {
                //10 player
                if (player.compareTo(topPlayers.get(i)) < 0) break;
                if (topPlayers.size() > i + 1)
                    topPlayers.set(i + 1, topPlayers.get(i)); // puts the above player down

                topPlayers.set(i, player);
            }

        }
        return topPlayers;
    }


    public LeaderboardPlayer getTopPlayer(int position) {
        if (updating) return null;
        return topTenPlayers.get(position - 1);
    }






}
