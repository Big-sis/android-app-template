package fr.wildcodeschool.vyfe.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class TagEntity {
    private String color;
    private String name;
    private HashMap<String, ArrayList<TimeEntity>> Times;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, ArrayList<TimeEntity>> getTimes() {
        return Times;
    }

    public void setTimes(HashMap<String, ArrayList<TimeEntity>> times) {
        Times = times;
    }
}
