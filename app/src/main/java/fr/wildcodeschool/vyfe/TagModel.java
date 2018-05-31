package fr.wildcodeschool.vyfe;

import android.os.Parcel;
import android.os.Parcelable;

public class TagModel implements Parcelable {
    private int color;
    private String name;

    public TagModel(int color, String name) {
        this.color = color;
        this.name = name;
    }

    protected TagModel(Parcel in) {
        color = in.readInt();
        name = in.readString();
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(color);
        parcel.writeString(name);
    }
}
