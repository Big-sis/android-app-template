package fr.vyfe.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class TagEntity {
    private String color;
    private String name;
    private HashMap<String, ArrayList<TimeEntity>> Times;
    private int leftOffset;
    private int rigthOffset;


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

    public int getLeftOffset() {
        return leftOffset;
    }

    public void setLeftOffset(int leftOffset) {
        this.leftOffset = leftOffset;
    }

    public int getRigthOffset() {
        return rigthOffset;
    }

    public void setRigthOffset(int rigthOffset) {
        this.rigthOffset = rigthOffset;
    }
}
