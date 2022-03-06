package ru.myitschool.vsu2021.markyachnyj.the_project.logic;

public class Grade {
    private int number;
    private int topic_count;
    private int topic_completed;

    public Grade(int number, int topic_count, int topic_completed) {
        this.number = number;
        this.topic_count = topic_count;
        this.topic_completed = topic_completed;
    }

    public float getProgress(){
        return 1f*topic_count/topic_completed;
    }

    public int getNumber() {
        return number;
    }

    public int getTopic_count() {
        return topic_count;
    }

    public int getTopic_completed() {
        return topic_completed;
    }
}
