package fr.wildcodeschool.vyfe.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TagModel implements Parcelable{

    private String tagId;
    private String color;
    private String tagName;
    private ArrayList<TimeModel> times;
    private String taggerId;

    protected TagModel(Parcel in) {
        tagId = in.readString();
        color = in.readString();
        tagName = in.readString();
        taggerId = in.readString();
        leftOffset = in.readString();
        rigthOffset = in.readString();
        times = in.createTypedArrayList(TimeModel.CREATOR);
    }

    public static final Creator<TagModel> CREATOR = new Creator<TagModel>() {
        @Override
        public TagModel createFromParcel(Parcel in) {
            return new TagModel(in);
        }

        @Override
        public TagModel[] newArray(int size) {
            return new TagModel[size];
        }
    };

    public String getLeftOffset() {
        return leftOffset;
    }

    public void setLeftOffset(String leftOffset) {
        this.leftOffset = leftOffset;
    }

    public String getRigthOffset() {
        return rigthOffset;
    }

    public void setRigthOffset(String rigthOffset) {
        this.rigthOffset = rigthOffset;
    }

    private String leftOffset;
    private String rigthOffset;

    public TagModel() {
    }

    public TagModel(String color, String tagName, String fkTagSet, String rigthOffset) {
        this.color = color;
        this.tagName = tagName;
    }

    public TagModel(String tagId, String color, String tagName) {
        this.tagId = tagId;
        this.color = color;
        this.tagName = tagName;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public ArrayList<TimeModel> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<TimeModel> times) {
        this.times = times;
    }

    public int getCount() {
        return times != null ? times.size() : 0;
    }

    public String getTaggerId() {
        return taggerId;
    }

    public void setTaggerId(String taggerId) {
        this.taggerId = taggerId;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tagId);
        dest.writeString(color);
        dest.writeString(tagName);
        dest.writeString(taggerId);
        dest.writeString(leftOffset);
        dest.writeString(rigthOffset);
        dest.writeTypedList(times);
    }
}


