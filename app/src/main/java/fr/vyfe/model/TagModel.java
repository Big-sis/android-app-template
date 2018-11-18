package fr.vyfe.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TagModel implements Parcelable{

    private String tagId;
    private ColorModel color;
    private String tagName;
    private ArrayList<TimeModel> times;
    private String taggerId;
    private int leftOffset;
    private int rigthOffset;


    public TagModel() {
        leftOffset = 4;
        rigthOffset = 3;
    }

    protected TagModel(Parcel in) {
        tagId = in.readString();
        color = in.readParcelable((ClassLoader) ColorModel.CREATOR);
        tagName = in.readString();
        taggerId = in.readString();
        leftOffset = in.readInt();
        rigthOffset = in.readInt();
        times = in.createTypedArrayList(TimeModel.CREATOR);
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count =0;

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

    public int getLeftOffset() {
        return leftOffset;
    }

    public void setLeftOffset(int leftOffset) {
        this.leftOffset = leftOffset;
    }

    public int getRigthOffset() {
        return rigthOffset;
    }

    public void setRigthOffset(int rigthOffset) {
        this.rigthOffset = rigthOffset;
    }

    public ColorModel getColor() {
        return color;
    }

    public void setColor(ColorModel color) {
        this.color = color;
    }

    public String getTagName() {
        return tagName;
    }

    public void setName(String tagName) {
        this.tagName = tagName;
    }

    public ArrayList<TimeModel> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<TimeModel> times) {
        this.times = times;
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
        dest.writeParcelable(color, 0);
        dest.writeString(tagName);
        dest.writeString(taggerId);
        dest.writeInt(leftOffset);
        dest.writeInt(rigthOffset);
        dest.writeTypedList(times);
    }
}


