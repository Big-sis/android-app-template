package fr.wildcodeschool.vyfe.model;


import android.media.MediaMetadataRetriever;

import java.util.ArrayList;

import fr.wildcodeschool.vyfe.Constants;


public class SessionModelBDD2 {

    private String author;
    private String date;
    private String idAndroid;
    private String idTagSet;
    private String name;
    private String pathApp;
    private String videoLink;
    private String thumbnailUrl;
    private String description;

    private ArrayList<TagsSessionBB2> tagsSessions;

    public SessionModelBDD2(String author, String date, String idAndroid, String idTagSet, String name, String pathApp, String videoLink, String thumbnailUrl, String description, ArrayList<TagsSessionBB2> tagsSessions) {
        this.author = author;
        this.date = date;
        this.idAndroid = idAndroid;
        this.idTagSet = idTagSet;
        this.name = name;
        this.pathApp = pathApp;
        this.videoLink = videoLink;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.tagsSessions = tagsSessions;
    }

    public SessionModelBDD2(String author, String date, String idAndroid, String idTagSet, String name, String pathApp, String videoLink, String thumbnailUrl, String description) {
    }

    public SessionModelBDD2() {

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIdAndroid() {
        return idAndroid;
    }

    public void setIdAndroid(String idAndroid) {
        this.idAndroid = idAndroid;
    }

    public String getIdTagSet() {
        return idTagSet;
    }

    public void setIdTagSet(String idTagSet) {
        this.idTagSet = idTagSet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathApp() {
        return pathApp;
    }

    public void setPathApp(String pathApp) {
        this.pathApp = pathApp;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<TagsSessionBB2> getTagsSessions() {
        return tagsSessions;
    }

    public void setTagsSessions(ArrayList<TagsSessionBB2> tagsSessions) {
        this.tagsSessions = tagsSessions;
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




