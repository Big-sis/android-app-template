package fr.wildcodeschool.vyfe.model;

import java.util.ArrayList;

public class TimesTaggerSessionBB2 {
    private String idTagger;
    private ArrayList<TimeModel> times;


    public TimesTaggerSessionBB2(String idTagger, ArrayList<TimeModel> times) {
        this.idTagger = idTagger;
        this.times = times;
    }

    public String getIdTagger() {
        return idTagger;
    }

    public void setIdTagger(String idTagger) {
        this.idTagger = idTagger;
    }



    public ArrayList<TimeModel> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<TimeModel> times) {
        this.times = times;
    }
}