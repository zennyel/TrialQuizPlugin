package com.zennyel.command;

import com.zennyel.QuizPlugin;
import com.zennyel.database.MariaDB;
import com.zennyel.quiz.QuizManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuizCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)){
            System.out.println("Command only to players!");
            return false;
        }
        Player player = (Player) commandSender;

        QuizPlugin instance = QuizPlugin.getPlugin(QuizPlugin.class);
        MariaDB database = instance.getDatabase();
        QuizManager manager = instance.getManager();

        if(!player.hasPermission("quiz.create") || !player.hasPermission("quiz.join")) {
            player.sendMessage("                                           ");
            player.sendMessage("§c§l[QuizEvent] §cYou dont have permission to use this command");
            player.sendMessage("                                           ");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            return false;
        }


        if(strings.length == 0) {
            String[] quizCommands = {
                    "\n§c§l[QuizEvent] §cTo reply a event use: /quiz reply",
                    "§c§l[QuizEvent] §cTo see the leaderboard use: /quiz leaderboard",
                    "§c§l[QuizEvent] §cTo leave the reply mode use: /quiz exit"
            };
            if(player.hasPermission("quiz.create")) {
                String[] adminCommands = {
                        "§c§l[QuizEvent] §cTo create a quiz use: /quiz create <category>",
                        "§c§l[QuizEvent] §cTo close a event use /quiz close\n"
                };
                player.sendMessage(adminCommands);
            }
            player.sendMessage(quizCommands);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            return false;
        }


        if(player.hasPermission("quiz.create")) {

            if (strings[0].equalsIgnoreCase("close")) {
                if (!manager.getOnQuizEvent()) {
                    player.sendMessage("                                           ");
                    player.sendMessage("§5§l[QuizEvent] §cNo event to close!");
                    player.sendMessage("                                           ");
                } else {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle("§c§lEVENT CLOSED!", "The quiz event has been closed", 1, 20, 10);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                    }
                    Bukkit.broadcastMessage("                 ");
                    Bukkit.broadcastMessage("§5§l[QuizEvent] §4§l" + player.getName() + "§f closed the quiz event!!");
                    Bukkit.broadcastMessage("§5§l[QuizEvent] §fQuiz event closing!");
                    Bukkit.broadcastMessage("                   ");
                    manager.closeEvent();
                }
                return false;
            }


            if (player.hasPermission("quiz.create")) {
                switch (strings[0]) {
                    case "close":
                        if (!manager.getOnQuizEvent()) {
                            player.sendMessage("\n§5§l[QuizEvent] §cNo event to close!\n");
                            return false;
                        }
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.sendTitle("§c§lEVENT CLOSED!", "The quiz event has been closed", 1, 20, 10);
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                        }
                        Bukkit.broadcastMessage("\n§5§l[QuizEvent] §4§l" + player.getName() + "§f closed the quiz event!!\n§5§l[QuizEvent] §fQuiz event closing!\n");
                        manager.closeEvent();
                        break;

                    case "create":
                        if (strings.length < 2) {
                            player.sendMessage("\n§c§l[QuizEvent] §cUse: /quiz create <category>\n");
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                            return false;
                        }

                        if (manager.isCreatingQuiz(player)) {
                            player.sendMessage("\n§c§l[QuizEvent] §cYou are creating a quiz!\n");
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                            return false;
                        }

                        if (manager.getOnQuizEvent()) {
                            player.sendMessage("\n§c§l[QuizEvent] Aleardy on quiz event!\n");
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                            return false;
                        }

                        String category = strings[1];
                        manager.addMessage(player, category);
                        manager.setCreatingQuiz(player, true);

                        StringBuilder message = new StringBuilder();
                        message.append("\n§5§l[QuizEvent] §fCategory: §6§l").append(category).append("\n§5§l[QuizEvent] §fNow, type the question!\n");
                        player.sendMessage(message.toString());
                        player.sendMessage("\n");
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                        return false;

                    case "leaderboard":
                        if (database.getPlayerPoints() == null) {
                            player.sendMessage("\n§5§l[QuizEvent] §fBe the first Player on leaderboard!\n");
                        }
                        database.displayTopTenPlayers(player);
                        return false;
                }

                if (manager.getOnQuizEvent()) {
                    switch (strings[0]) {
                        case "reply":
                            StringBuilder replyMessage = new StringBuilder();
                            replyMessage.append("\n§5§l[QuizEvent] §fType your answer on chat!\n");
                            player.sendMessage(replyMessage.toString());
                            manager.updatePoints(player);
                            manager.setJoining(player, true);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                            return false;

                        case "exit":
                            manager.setJoining(player, false);
                            StringBuilder exitMessage = new StringBuilder();
                            exitMessage.append("§5§l[QuizEvent] §fYou exited the quiz!\n");
                            player.sendMessage(exitMessage.toString());
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                            return false;
                    }
                } else {
                    player.sendMessage("\n§c§l[QuizEvent] §cEvent not happening at this moment!\n");
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    return false;
                }

            } else {
                player.sendMessage("                                           ");
                player.sendMessage("§c§l[QuizEvent] §cEvent not happening on this moment!");
                player.sendMessage("                                           ");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                return false;
            }
        }

        return false;
    }
}
