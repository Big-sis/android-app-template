package fr.vyfe.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import fr.vyfe.Constants;

public class TagModel implements Parcelable{

    private String id;
    private ColorModel color;
    private String name;
    private ArrayList<TimeModel> times;
    private String taggerId;
    private int leftOffset;
    private int rigthOffset;


    public TagModel() {
        leftOffset = Constants.STANDARD_LEFT_OFFSET;
        rigthOffset = Constants.STANDARD_RIGHT_OFFSET;
    }

    protected TagModel(Parcel in) {
        id = in.readString();
        color = in.readParcelable(ColorModel.class.getClassLoader());
        name = in.readString();
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

    public String getName() {
        return name;
    }

    public void setName(String tagName) {
        this.name = tagName;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(color, 0);
        dest.writeString(name);
        dest.writeString(taggerId);
        dest.writeInt(leftOffset);
        dest.writeInt(rigthOffset);
        dest.writeTypedList(times);
    }
}


