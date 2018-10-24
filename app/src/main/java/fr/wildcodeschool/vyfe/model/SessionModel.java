package fr.wildcodeschool.vyfe.model;

import android.media.MediaMetadataRetriever;

import java.util.ArrayList;
import java.util.HashMap;

import fr.wildcodeschool.vyfe.Constants;

public class SessionModel {
    private String name;
    private String author;
    private String serverVideoLink;
    private String date;
    private String idSession;
    private String idTagSet;
    private ArrayList<TagModel> tags;
    private String description;
    private String idAndroid;
    private String deviceVideoLink;
    private String thumbnail;


    public SessionModel(String name, String author, String videoLink, String date, String idSession, String idTagSet) {
        this.name = name;
        this.author = author;
        this.serverVideoLink = videoLink;
        this.date = date;
        this.idSession = idSession;
        this.idTagSet = idTagSet;
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
        return serverVideoLink;
    }

    public void setVideoLink(String videoLink) {
        this.serverVideoLink = videoLink;
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

    public String getIdAndroid() {
        return idAndroid;
    }

    public void setIdAndroid(String idAndroid) {
        this.idAndroid = idAndroid;
    }

    public int getDuration(){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        //use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(this.deviceVideoLink);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int timeInMillisec = Integer.parseInt(time);

        retriever.release();

        return timeInMillisec / Constants.UNIT_TO_MILLI_FACTOR;
    }

    public String getServerVideoLink() {
        return serverVideoLink;
    }

    public void setServerVideoLink(String serverVideoLink) {
        this.serverVideoLink = serverVideoLink;
    }

    public ArrayList<TagModel> getTags() {
        return tags;
    }

    public void setTags(ArrayList<TagModel> tags) {
        this.tags = tags;
    }

    public String getDeviceVideoLink() {
        return deviceVideoLink;
    }

    public void setDeviceVideoLink(String deviceVideoLink) {
        this.deviceVideoLink = deviceVideoLink;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
