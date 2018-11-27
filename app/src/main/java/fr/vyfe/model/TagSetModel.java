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

    public TagSetModel() {}

    protected TagSetModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        tagTemplates = in.createTypedArrayList(TemplateModel.CREATOR);
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
    }

    public static final DiffUtil.ItemCallback<TagSetModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<TagSetModel>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull TagSetModel oldTagSet, @NonNull TagSetModel newTagSet) {
                    // User properties may have changed if reloaded from the DB, but ID is fixed
                    return oldTagSet.getId() == newTagSet.getId();
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull TagSetModel oldTagSet, @NonNull TagSetModel newTagSet) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return oldTagSet.equals(newTagSet);
                }
            };
}
