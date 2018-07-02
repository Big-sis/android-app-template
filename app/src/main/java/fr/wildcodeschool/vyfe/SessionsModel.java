package fr.wildcodeschool.vyfe;

import java.util.ArrayList;
import java.util.HashMap;

public class SessionsModel {
    private String name;
    private String author;
    private String videoLink;
    private String date;
    private String idSession;
    private String idTagSet;
    private HashMap<String, TagModel> tags;


    public SessionsModel(String name, String author, String videoLink, String date, String idSession, String idTagSet) {
        this.name = name;
        this.author = author;
        this.videoLink = videoLink;
        this.date = date;
        this.idSession = idSession;
        this.idTagSet = idTagSet;
        this.tags = new HashMap<>();
    }

    public SessionsModel(){}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdSession() {
        return idSession;
    }

    public void setIdSession(String idSession) {
        this.idSession = idSession;
    }

    public String getIdTagSet() {
        return idTagSet;
    }

    public void setIdTagSet(String idTagSet) {
        this.idTagSet = idTagSet;
    }

    public HashMap<String, TagModel> getTags() {
        return tags;
    }

    public void setTags(HashMap<String, TagModel> tags) {
        this.tags = tags;
    }
}
