package fr.wildcodeschool.vyfe.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class TagSetModel implements Parcelable {
    private String id;
    private String name;
    private ArrayList<TagModel> tags;

    public TagSetModel() {}

    public TagSetModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    protected TagSetModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        tags = in.createTypedArrayList(TagModel.CREATOR);
    }

    public static final Creator<TagSetModel> CREATOR = new Creator<TagSetModel>() {
        @Override
        public TagSetModel createFromParcel(Parcel in) {
            return new TagSetModel(in);
        }

        @Override
        public TagSetModel[] newArray(int size) {
            return new TagSetModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<TagModel> getTags() {
        return tags;
    }

    public void setTags(ArrayList<TagModel> tags) {
        this.tags = tags;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeTypedList(tags);
    }
}
