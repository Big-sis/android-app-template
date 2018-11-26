package fr.vyfe.model;

import android.os.Parcel;
import android.os.Parcelable;

import fr.vyfe.Constants;

public class TemplateModel implements Parcelable, VyfeModel {

    private String id;
    private ColorModel color;
    private String name;
    private int leftOffset;
    private int rigthOffset;

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
    }
}


