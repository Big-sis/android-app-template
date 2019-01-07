package fr.vyfe.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import fr.vyfe.Constants;

public class TemplateModel implements Parcelable, VyfeModel, Comparable<TemplateModel> {

    private String id;
    private ColorModel color;
    private String name;
    private int leftOffset;
    private int rigthOffset;
    private int count = 0;
    private int position;
    private boolean touch = false;

    public TemplateModel() {
        leftOffset = Constants.STANDARD_LEFT_OFFSET;
        rigthOffset = Constants.STANDARD_RIGHT_OFFSET;
    }

    protected TemplateModel(Parcel in) {
        id = in.readString();
        color = in.readParcelable(ColorModel.class.getClassLoader());
        name = in.readString();
        leftOffset = in.readInt();
        rigthOffset = in.readInt();
        position= in.readInt();
    }

    public static final Creator<TemplateModel> CREATOR = new Creator<TemplateModel>() {
        @Override
        public TemplateModel createFromParcel(Parcel in) {
            return new TemplateModel(in);
        }

        @Override
        public TemplateModel[] newArray(int size) {
            return new TemplateModel[size];
        }
    };

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void incrCount() {
        this.count++;
    }

    public boolean isTouch() {
        return touch;
    }

    public void setTouch(boolean touch) {
        this.touch = touch;
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
        dest.writeInt(leftOffset);
        dest.writeInt(rigthOffset);
        dest.writeInt(position);
    }

    @Override
    public int compareTo(@NonNull TemplateModel o) {
        return (this.position - o.getPosition());
    }
}


