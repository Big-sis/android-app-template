package fr.wildcodeschool.vyfe;

import java.util.ArrayList;

public class TagSessionModel {
    private String tagName;
    private ArrayList<TimeModel> times;

    public TagSessionModel() {
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
}
