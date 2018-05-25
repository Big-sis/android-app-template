package fr.wildcodeschool.vyfe;

import android.os.Parcel;
import android.os.Parcelable;

public class ObservationItemsModel implements Parcelable {
    private int color;
    private String name;

    public ObservationItemsModel(int color, String name) {
        this.color = color;
        this.name = name;
    }

    protected ObservationItemsModel(Parcel in) {
        color = in.readInt();
        name = in.readString();
    }

    public static final Creator<ObservationItemsModel> CREATOR = new Creator<ObservationItemsModel>() {
        @Override
        public ObservationItemsModel createFromParcel(Parcel in) {
            return new ObservationItemsModel(in);
        }

        @Override
        public ObservationItemsModel[] newArray(int size) {
            return new ObservationItemsModel[size];
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
