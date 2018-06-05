package fr.wildcodeschool.vyfe;

import java.util.ArrayList;

public class SingletonTagsSets {
    public static SingletonTagsSets sInstance = null;


    private ArrayList<TagSetsModel> mTagsSetsList = new ArrayList<>();

    private SingletonTagsSets() {
    }

    public static SingletonTagsSets getInstance() {
        if (sInstance == null) {
            sInstance = new SingletonTagsSets();
        }
        return sInstance;
    }

    public ArrayList<TagSetsModel> getmTagsSetsList() {
        return mTagsSetsList;
    }

    public void setmTagsList(ArrayList<TagSetsModel> mTagsSetsList) {
        this.mTagsSetsList = mTagsSetsList;
    }
}
