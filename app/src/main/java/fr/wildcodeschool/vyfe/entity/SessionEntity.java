package fr.wildcodeschool.vyfe.entity;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

import fr.wildcodeschool.vyfe.model.TagModel;

@IgnoreExtraProperties
public class SessionEntity {

    private String name;
    private String author;
    private String videoLink;
    private String date;
    private String idSession;
    private String idTagSet;
    private HashMap<String, TagModel> tags;
    private String description;
    private String idAndroid;
    private String thumbnailUrl;


    public SessionEntity(){}


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdAndroid() {
        return idAndroid;
    }

    public void setIdAndroid(String idAndroid) {
        this.idAndroid = idAndroid;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
