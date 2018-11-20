package fr.vyfe.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ColorModel implements Parcelable {
    private String id;
    private int name;
    private int image;


    public ColorModel(String id, int name, int image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }


    protected ColorModel(Parcel in) {
        id = in.readString();
        name = in.readInt();
        image = in.readInt();
    }

    public static final Creator<ColorModel> CREATOR = new Creator<ColorModel>() {
        @Override
        public ColorModel createFromParcel(Parcel in) {
            return new ColorModel(in);
        }

        @Override
        public ColorModel[] newArray(int size) {
            return new ColorModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(name);
        dest.writeInt(image);
    }
}
