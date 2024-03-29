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
    private int rightOffset;
    private Integer count;
    private int position;
    private boolean touch = false;

    public TemplateModel() {
        leftOffset = Constants.STANDARD_LEFT_OFFSET;
        rightOffset = Constants.STANDARD_RIGHT_OFFSET;
    }

    protected TemplateModel(Parcel in) {
        id = in.readString();
        color = in.readParcelable(ColorModel.class.getClassLoader());
        name = in.readString();
        leftOffset = in.readInt();
        rightOffset = in.readInt();
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

    public int getRightOffset() {
        return rightOffset;
    }

    public void setRightOffset(int rightOffset) {
        this.rightOffset = rightOffset;
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void incrCount() {
        if(count ==null) count =0;
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
        dest.writeInt(rightOffset);
        dest.writeInt(position);
    }

    @Override
    public int compareTo(@NonNull TemplateModel o) {
        return (this.position - o.getPosition());
    }
}


