package fr.wildcodeschool.vyfe.model;

import java.util.ArrayList;

public class TagModel {

    private String tagId;
    private String color;
    private String tagName;
    private ArrayList<TimeModel> times;
    private String taggerId;

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

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public ArrayList<TimeModel> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<TimeModel> times) {
        this.times = times;
    }

    public int getCount() {
        return times != null ? times.size() : 0;
    }

    public String getTaggerId() {
        return taggerId;
    }

    public void setTaggerId(String taggerId) {
        this.taggerId = taggerId;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
}


