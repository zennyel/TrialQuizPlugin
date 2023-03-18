package com.zennyel.event;

import com.zennyel.quiz.QuizManager;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QuizEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private String message;
    private QuizManager manager;

    public QuizEvent(QuizManager manager) {
        this.manager = manager;
    }

    public QuizManager getManager() {
        return manager;
    }

    public String getMessage() {
        return message;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
