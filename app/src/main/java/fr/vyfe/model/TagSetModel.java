package fr.vyfe.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import java.util.ArrayList;

public class TagSetModel implements Parcelable, VyfeModel {
    private String id;
    private String name;
    private ArrayList<TemplateModel> tagTemplates;
    private String owner;
    private boolean shared;

    public TagSetModel() {}

    protected TagSetModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        tagTemplates = in.createTypedArrayList(TemplateModel.CREATOR);
        owner = in.readString();
        shared  = (in.readInt() == 0) ? false : true;
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

    public ArrayList<TemplateModel> getTemplates() {
        return tagTemplates;
    }

    public void setTagTemplates(ArrayList<TemplateModel> tagTemplates) {
        this.tagTemplates = tagTemplates;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public void addTag(TemplateModel tag){
        this.tagTemplates.add(tag);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeTypedList(tagTemplates);
        dest.writeString(owner);
        dest.writeInt(shared ? 1 : 0);
    }
}
