package com.zennyel;

import com.zennyel.command.QuizCommand;
import com.zennyel.database.MariaDB;
import com.zennyel.listener.onPlayerChat;
import com.zennyel.quiz.Quiz;
import com.zennyel.quiz.QuizManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class QuizPlugin extends JavaPlugin {
    private MariaDB database;
    private QuizManager manager;
    private Boolean onQuizEvent;
    private Quiz quiz;

    @Override
    public void onEnable() {
        connect();
        manager = new QuizManager();
        onQuizEvent = false;
        saveDefaultConfig();
        registerCommands();
        registerEvents();
    }

    @Override
    public void onDisable() {
    }

    public void registerCommands(){
        getCommand("quiz").setExecutor(new QuizCommand());
    }

    public void registerEvents(){
        Bukkit.getPluginManager().registerEvents(new onPlayerChat(), this);
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public Boolean getOnQuizEvent() {
        return onQuizEvent;
    }

    public void setOnQuizEvent(Boolean onQuizEvent) {
        this.onQuizEvent = onQuizEvent;
    }

    public void connect(){
        database = new MariaDB(getConfig());
        database.connect();
        database.CreateTable();
    }

    public MariaDB getDatabase() {
        return database;
    }

    public QuizManager getManager() {
        return manager;
    }
}
