package com.zennyel.listener;

import com.zennyel.QuizPlugin;
import com.zennyel.event.QuizEvent;
import com.zennyel.quiz.Quiz;
import com.zennyel.quiz.QuizManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.List;

public class onPlayerChat implements Listener {

    private QuizPlugin quizPlugin = QuizPlugin.getPlugin(QuizPlugin.class);
    private QuizManager manager = quizPlugin.getManager();


    @EventHandler
    public void onChat(PlayerChatEvent e){
        Player p = e.getPlayer();

        if(manager.isCreatingQuiz(p)){

            List<String> messages = manager.getMessages(p);

            if(messages.size() == 3){

                if (!e.getMessage().equalsIgnoreCase("confirm") && !e.getMessage().equalsIgnoreCase("cancel")) {
                    p.sendMessage("§c§l[QuizEvent] please type §L > §nCANCEL < §cor §l> §nCONFIRM §c<");
                    e.setCancelled(true);
                    return;
                }
                if(e.getMessage().equalsIgnoreCase("confirm")){
                    manager.setQuiz(new Quiz(messages.get(0), messages.get(1), messages.get(2)));
                    manager.setCreatingQuiz(p, false);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    e.setCancelled(true);
                }else if(e.getMessage().equalsIgnoreCase("cancel")) {
                    manager.setCreatingQuiz(p, false);
                    p.sendMessage("§c§l[QuizEvent] §cEvent cancelled!");
                    e.setCancelled(true);
                    manager.closeEvent();
                    return;
                }

                String category = messages.get(0);
                String question = messages.get(1);
                String correctAwnser = messages.get(2);

                manager.setQuiz(new Quiz(category, question, correctAwnser));

                for(Player player : Bukkit.getOnlinePlayers()){
                    player.sendTitle("§5§lEVENT STARTED!", "The quiz event has been started!", 1,20, 10);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                }
                String message = String.format("%s\n%s\n%s\n%s\n%s\n",
                        "",
                        "§5§l[QuizEvent] §fQuiz event starting!",
                        "§5§l[QuizEvent] §fCategory: §e§l" + category,
                        "§5§l[QuizEvent] §fQuestion: §6§l" + question.toUpperCase(),
                        "§5§l[QuizEvent] §fuse: /quiz reply",
                        ""
                );
                Bukkit.broadcastMessage(message);
                Bukkit.getPluginManager().callEvent(new QuizEvent(manager));
                manager.setOnQuizEvent(true);
            }

            switch (messages.size()) {
                case 1:

                    p.sendMessage("§5§l[QuizEvent] §fYour question: §6§l" + e.getMessage());

                    messages.add(e.getMessage());

                    p.sendMessage("§5§l[QuizEvent] §fNext, type the awnser!");
                    p.sendMessage("                                              ");

                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                    e.setCancelled(true);
                break;
                case 2:
                    messages.add(e.getMessage());
                    manager.setAwnser(e.getMessage());

                    String message = String.format("%s\n%s\n%s\n%s\n\n",
                            "§5§l[QuizEvent] §fYour awnser: §6§l" + e.getMessage(),
                            "",
                            "§5§l[QuizEvent] §ftype > §2§lCONFIRM §f< to start the event!",
                            "§5§l[QuizEvent] §ftype > §4§lCANCEL §f< to start the event!",
                            ""
                    );

                    p.sendMessage(message);

                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                e.setCancelled(true);
                break;
            }
            return;
        }


        if(manager.getIsJoining() == null) {
            return;
        }

        if(manager.isJoining(p)){

            e.setCancelled(true);
            p.sendMessage("§5§l[QuizEvent] §fYour reply: §6§l" + e.getMessage());
            if(manager.getAwnser().equalsIgnoreCase(e.getMessage())){
                manager.setWinner(p);
                manager.closeEvent();
                return;
            }

            p.sendMessage("§5§l[QuizEvent] §fWrong awnser, try again or use: /quiz exit to leave!");
        }


    }

}
