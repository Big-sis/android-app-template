package fr.vyfe.model;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import fr.vyfe.entity.ObserverEntity;

public class SessionModel implements Parcelable, VyfeModel, Comparable<SessionModel> {

    private String name;
    private String author;
    private String serverVideoLink;
    private Date date;
    private String id;
    private ArrayList<TagModel> Tags;
    private String description;
    private String idAndroid;
    private String deviceVideoLink;
    private String thumbnail;
    private boolean cooperative;
    private boolean recording;
    private int duration;
    private ArrayList<ObserverModel> observers;
    private TagSetModel tagsSet;
    private OwnerModel owner;

    public SessionModel(String name, String author, String videoLink, Date date, String idSession, String idTagSet) {
        this();
        this.name = name;
        this.author = author;
        this.serverVideoLink = videoLink;
        this.date = date;
        this.id = idSession;
    }

    public SessionModel(String name, ArrayList<TagModel> tags, String idTagSet) {
        this();
        this.name = name;
        this.Tags = tags;
    }

    public SessionModel() {
        this.date = new Date();
    }


    public SessionModel(String name, String author, String videoLink, Date date, String idSession, String idTagSet, String idAndroid) {
        this(name, author, videoLink, date, idSession, idTagSet);
        this.idAndroid = idAndroid;
    }

    public SessionModel(Parcel in) {
        name = in.readString();
        author = in.readString();
        serverVideoLink = in.readString();
        date = new Date(in.readLong());
        id = in.readString();
        Tags = in.createTypedArrayList(TagModel.CREATOR);
        description = in.readString();
        idAndroid = in.readString();
        deviceVideoLink = in.readString();
        thumbnail = in.readString();
        duration = in.readInt();

    }

    public OwnerModel getOwner() {
        return owner;
    }

    public void setOwner(OwnerModel owner) {
        this.owner = owner;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isCooperative() {
        return cooperative;
    }

    public void setCooperative(boolean cooperative) {
        this.cooperative = cooperative;
    }

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public TagSetModel getTagsSet() {
        return tagsSet;
    }

    public void setTagsSet(TagSetModel tagsSet) {
        this.tagsSet = tagsSet;
    }

    /**
    public int getDuration() {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        //use one of overloaded setDataSource() functions to set your data source
        //TODO : verifier code
        if (this.deviceVideoLink == null && serverVideoLink != null) {
            return -1;
        } else retriever.setDataSource(this.deviceVideoLink);

        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int timeInMillisec = Integer.parseInt(time);

        retriever.release();

        return timeInMillisec;
    }**/

    public String getServerVideoLink() {
        return serverVideoLink;
    }

    public void setServerVideoLink(String serverVideoLink) {
        this.serverVideoLink = serverVideoLink;
    }

    public ArrayList<TagModel> getTags() {
        return Tags;
    }

    public void setTags(ArrayList<TagModel> tags) {
        this.Tags = tags;
    }

    public String getDeviceVideoLink() {
        return deviceVideoLink;
    }

    public void setDeviceVideoLink(String deviceVideoLink) {
        this.deviceVideoLink = deviceVideoLink;
    }

    public ArrayList<ObserverModel> getObservers() {
        return observers;
    }

    public void setObservers(ArrayList<ObserverModel> observers) {
        this.observers = observers;
    }

    // TODO : Display Vimeo thumbnail once available
    public Bitmap getThumbnail() {
        return ThumbnailUtils.createVideoThumbnail(getDeviceVideoLink(), MediaStore.Images.Thumbnails.MINI_KIND);
    }

    public String getThumbnailUrl() {
        return this.thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(author);
        dest.writeString(serverVideoLink);
        dest.writeLong(date.getTime());
        dest.writeString(id);
        dest.writeTypedList(Tags);
        dest.writeString(description);
        dest.writeString(idAndroid);
        dest.writeString(deviceVideoLink);
        dest.writeString(thumbnail);
        dest.writeInt(duration);
    }

    public String getFormatDate() {
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yy HH:mm");
        return dt.format(new Date(this.date.getTime()));
    }

    @Override
    public int compareTo(@NonNull SessionModel o) {
        return ((int) (this.date.getTime() - o.getDate().getTime()));
    }

    public static final Creator<SessionModel> CREATOR = new Creator<SessionModel>() {
        @Override
        public SessionModel createFromParcel(Parcel in) {
            return new SessionModel(in);
        }

        @Override
        public SessionModel[] newArray(int size) {
            return new SessionModel[size];
        }
    };
}
