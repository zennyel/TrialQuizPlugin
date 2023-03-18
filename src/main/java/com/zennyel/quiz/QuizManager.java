package com.zennyel.quiz;

import com.zennyel.QuizPlugin;
import com.zennyel.database.MariaDB;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizManager {

    private Map<Player, List<String>> messages = new HashMap<>();
    private Map<Player, Boolean> creatingQuiz = new HashMap<>();
    private Map<Player, Boolean> isJoining = new HashMap<>();
    private QuizPlugin instance = QuizPlugin.getPlugin(QuizPlugin.class);
    private MariaDB database = instance.getDatabase();
    private String awnser = null;


    public QuizManager() {
    }


    public void setAwnser(String awnser) {
        this.awnser = awnser;
    }

    public void closeQuiz(){
        for(Player p: Bukkit.getOnlinePlayers()){
            setJoining(p, false);
            instance.setOnQuizEvent(false);
        }
    }

    public void updatePoints(Player player){
        if(database.getPoints(player) < 0){
            database.insertPoints(player, 0);
        }
    }

    public void closeEvent(){
        for(Player p : Bukkit.getOnlinePlayers()){
            setJoining(p, false);
            setCreatingQuiz(p, false);
            messages.put(p, null);
        }
        awnser = null;
        closeQuiz();
    }

    public void setWinner(Player player){
        Bukkit.broadcastMessage("                   ");
        Bukkit.broadcastMessage("§5§l[QuizEvent] §f§a§l"+ player.getName() + " §fwon the quiz event!!");
        Bukkit.broadcastMessage("§5§l[QuizEvent] §fQuiz event closing, thank everyone!");
        Bukkit.broadcastMessage("                   ");
        for(Player p : Bukkit.getOnlinePlayers()){
            p.sendTitle("§f§a§lWinner: " + player.getName(), "Congratulations to won the quiz event!", 1,20, 10);
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
        }
        closeEvent();
        database.updatePoints(player, database.getPoints(player) + 1);
    }

    public String getAwnser() {
        return awnser;
    }
    public void setJoining(Player player, boolean isJoining){
        this.isJoining.put(player, isJoining);
    }

    public boolean isJoining(Player p){
        if(isJoining.get(p) == null){
            setJoining(p, false);
        }
        return isJoining.get(p);
    }

    public Map<Player, Boolean> getIsJoining() {
        return isJoining;
    }


    public void addMessage(Player player, String s){
        if(getMessages(player) == null){
            List<String> messageList = new ArrayList<>();
            messages.put(player, messageList);
        }
        List<String> messageList = getMessages(player);
        messageList.add(s);
        messages.put(player, messageList);
    }

    public void setCreatingQuiz(Player player, boolean isCreating){
        creatingQuiz.put(player, isCreating);
    }


    public boolean isCreatingQuiz(Player player){
        if(creatingQuiz.get(player) == null){
            return false;
        }
        return creatingQuiz.get(player);
    }

    public List<String> getMessages(Player p){
        return messages.get(p);
    }



}
