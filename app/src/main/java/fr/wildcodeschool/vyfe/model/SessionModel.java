package fr.wildcodeschool.vyfe.model;

import android.media.MediaMetadataRetriever;

import java.util.HashMap;

import fr.wildcodeschool.vyfe.Constants;

public class SessionModel {
    private String name;
    private String author;
    private String videoLink;
    private String date;
    private String idSession;
    private String idTagSet;
    private HashMap<String, TagModel> tags;
    private String description;
    private String idAndroid;


    public SessionModel(String name, String author, String videoLink, String date, String idSession, String idTagSet) {
        this.name = name;
        this.author = author;
        this.videoLink = videoLink;
        this.date = date;
        this.idSession = idSession;
        this.idTagSet = idTagSet;
        this.tags = new HashMap<>();
    }

    public SessionModel(){}

    public SessionModel(String name, String author, String videoLink, String date, String idSession, String idTagSet, String idAndroid) {
        this(name, author, videoLink, date, idSession, idTagSet);
        this.idAndroid = idAndroid;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getIdAndroid() {
        return idAndroid;
    }

    public void setIdAndroid(String idAndroid) {
        this.idAndroid = idAndroid;
    }

    public int getDuration(){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        //use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(this.videoLink);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int timeInMillisec = Integer.parseInt(time);

        retriever.release();

        return timeInMillisec / Constants.UNIT_TO_MILLI_FACTOR;
    }
}
