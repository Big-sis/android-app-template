package fr.wildcodeschool.vyfe.model;

import java.util.ArrayList;

public class TagModel {

    private String color;
    private String tagName;
    private ArrayList<TimeModel> times;
    // TODO : Expliciter le rôle de count
    private int count = 0;
    private String fkTagSet;

    public TagModel() {
    }

    public TagModel(String color, String tagName, String fkTagSet, String rigthOffset) {
        this.color = color;
        this.tagName = tagName;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return tagName;
    }

    public void setName(String tagName) {
        this.tagName = tagName;
    }

    public ArrayList<TimeModel> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<TimeModel> times) {
        this.times = times;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFkTagSet() {
        return this.fkTagSet;
    }
}


