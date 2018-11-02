package fr.wildcodeschool.vyfe.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TimeModel implements Parcelable {
    private int start;
    private int end;

    public TimeModel(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public TimeModel() {
    }


    protected TimeModel(Parcel in) {
        start = in.readInt();
        end = in.readInt();
    }

    public static final Creator<TimeModel> CREATOR = new Creator<TimeModel>() {
        @Override
        public TimeModel createFromParcel(Parcel in) {
            return new TimeModel(in);
        }

        @Override
        public TimeModel[] newArray(int size) {
            return new TimeModel[size];
        }
    };

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(start);
        dest.writeInt(end);
    }
}
