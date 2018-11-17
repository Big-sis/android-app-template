package fr.vyfe.model;

import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import fr.vyfe.Constants;

public class SessionModel implements Parcelable {
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

    public SessionModel(String name, ArrayList<TagModel> tags,String idTagSet ){
        this.name = name;
        this.tags = tags;
        this.idTagSet = idTagSet;
    }

    public SessionModel(){}

    public SessionModel(String name, String author, String videoLink, String date, String idSession, String idTagSet, String idAndroid) {
        this(name, author, videoLink, date, idSession, idTagSet);
        this.idAndroid = idAndroid;
    }


    public SessionModel(Parcel in) {
        name = in.readString();
        author = in.readString();
        serverVideoLink = in.readString();
        date = in.readString();
        idSession = in.readString();
        idTagSet = in.readString();
        tags = in.createTypedArrayList(TagModel.CREATOR);
        description = in.readString();
        idAndroid = in.readString();
        deviceVideoLink = in.readString();
        thumbnail = in.readString();
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(author);
        dest.writeString(serverVideoLink);
        dest.writeString(date);
        dest.writeString(idSession);
        dest.writeString(idTagSet);
        dest.writeTypedList(tags);
        dest.writeString(description);
        dest.writeString(idAndroid);
        dest.writeString(deviceVideoLink);
        dest.writeString(thumbnail);
    }
}
