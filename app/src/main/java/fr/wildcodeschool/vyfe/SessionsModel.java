package fr.wildcodeschool.vyfe;

import java.util.Date;

public class SessionsModel {
    private int id;
    private String name;
    private String author;
    private String fkGroup;
    private String videoLink;
    private String date;

    public SessionsModel(int id, String name, String author, String fkGroup, String videoLink, String date) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.fkGroup = fkGroup;
        this.videoLink = videoLink;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getFkGroup() {
        return fkGroup;
    }

    public void setFkGroup(String fkGroup) {
        this.fkGroup = fkGroup;
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
}