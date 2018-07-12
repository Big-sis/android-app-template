package fr.wildcodeschool.vyfe;

import android.util.Pair;

import java.util.ArrayList;

public class TagModel {

    private int color;
    private String tagName;
    private String fkTagSet;
    private String rigthOffset;
    private int start;
    private int end;
    private String leftOffset;
    private ArrayList<TimeModel> times;
    private int count = 0;

    public TagModel(int color, String tagName, ArrayList<TimeModel> times) {
        this.color = color;
        this.tagName = tagName;
        this.times = times;
    }

    public TagModel(int color, String tagName, String fkTagSet, String rigthOffset) {
        this.color = color;
        this.tagName = tagName;
        this.fkTagSet = fkTagSet;
        this.rigthOffset = rigthOffset;
    }

    public TagModel(int color, String tagName, String fkTagSet, int start, int end) {
        this.color = color;
        this.tagName = tagName;
        this.fkTagSet = fkTagSet;
        this.start = start;
        this.end = end;
    }

    public TagModel() {
    }

    public TagModel(int color, String tagName, String fkTagSet, String rigthOffset, String leftOffset) {
        this.color = color;
        this.tagName = tagName;
        this.fkTagSet = fkTagSet;
        this.rigthOffset = rigthOffset;
        this.leftOffset = leftOffset;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return tagName;
    }

    public void setName(String tagName) {
        this.tagName = tagName;
    }

    public String getFkTagSet() {
        return fkTagSet;
    }

    public void setFkTagSet(String fkTagSet) {
        this.fkTagSet = fkTagSet;
    }

    public String getRigthOffset() {
        return rigthOffset;
    }

    public void setRigthOffset(String rigthOffset) {
        this.rigthOffset = rigthOffset;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getLeftOffset() {
        return leftOffset;
    }

    public void setLeftOffset(String leftOffset) {
        this.leftOffset = leftOffset;
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
}



