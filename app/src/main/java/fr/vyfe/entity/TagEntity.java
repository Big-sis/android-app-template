package fr.vyfe.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class TagEntity {
    private String color;
    private String name;
    private HashMap<String, ArrayList<TimeEntity>> Times;
    private String leftOffset;
    private String rigthOffset;

    public String getLeftOffset() {
        return leftOffset;
    }

    public void setLeftOffset(String leftOffset) {
        this.leftOffset = leftOffset;
    }

    public String getRigthOffset() {
        return rigthOffset;
    }

    public void setRigthOffset(String rigthOffset) {
        this.rigthOffset = rigthOffset;
    }



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
