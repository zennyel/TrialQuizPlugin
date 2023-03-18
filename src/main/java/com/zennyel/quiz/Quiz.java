package com.zennyel.quiz;

public class Quiz {

    private String category;
    private String question;
    private String correctAwnser;


    public Quiz(String category, String question, String correctAwnser) {
        this.category = category;
        this.question = question;

        this.correctAwnser = correctAwnser;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAwnser() {
        return correctAwnser;
    }

    public void setCorrectAwnser(String correctAwnser) {
        this.correctAwnser = correctAwnser;
    }
}
