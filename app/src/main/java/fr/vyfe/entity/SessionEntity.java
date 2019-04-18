package fr.vyfe.entity;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class SessionEntity {

    private String author;
    private String name;
    private String description;
    private TagSetEntity tagSet;
    private String videoLink;
    private String pathApp;
    private String thumbnailUrl;
    private String idAndroid;
    private Boolean cooperative;
    private Boolean recording;
    private long timestamp;
    private int duration;
    private HashMap<String, ObserverEntity> observers;
    private HashMap<String, TagEntity> tags;
    private OwnerEntity owner;


    private String tagSetId;

    public SessionEntity() {
    }

    public Map<String, Object> toHashmap() {
        Map<String, Object> sessionMap = new HashMap<>();
        sessionMap.put("cooperative", cooperative);
        sessionMap.put("recording", recording);
        sessionMap.put("videoLink", videoLink);
        sessionMap.put("duration", duration);
        sessionMap.put("description", description);
        sessionMap.put("pathApp", pathApp);
        return sessionMap;

    }


    //Generated code


    public OwnerEntity getOwner() {
        return owner;
    }

    public void setOwner(OwnerEntity owner) {
        this.owner = owner;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public HashMap<String, ObserverEntity> getObservers() {
        return observers;
    }

    public void setObservers(HashMap<String, ObserverEntity> observers) {
        this.observers = observers;

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getIdAndroid() {
        return idAndroid;
    }

    public void setIdAndroid(String idAndroid) {
        this.idAndroid = idAndroid;
    }

    public String getTagSetId() {
        return tagSetId;
    }

    public void setTagSetId(String tagSetId) {
        this.tagSetId = tagSetId;
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

    public HashMap<String, TagEntity> getTags() {
        return tags;
    }

    public void setTags(HashMap<String, TagEntity> tags) {
        this.tags = tags;
    }

    public Boolean getCooperative() {
        return cooperative;
    }

    public void setCooperative(Boolean cooperative) {
        this.cooperative = cooperative;
    }

    public Boolean getRecording() {
        return recording;
    }

    public void setRecording(Boolean recording) {
        this.recording = recording;
    }

    public TagSetEntity getTagSet() {
        return tagSet;
    }

    public void setTagSet(TagSetEntity tagSet) {
        this.tagSet = tagSet;
    }
}
