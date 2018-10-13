package fr.wildcodeschool.vyfe.viewModel;

import java.util.ArrayList;

import fr.wildcodeschool.vyfe.model.TagSetModel;

public class SingletonTagsSets {
    public static SingletonTagsSets sInstance = null;


    private ArrayList<TagSetModel> mTagsSetsList = new ArrayList<>();


    private SingletonTagsSets() {
    }

    public static SingletonTagsSets getInstance() {
        if (sInstance == null) {
            sInstance = new SingletonTagsSets();
        }
        return sInstance;
    }

    public ArrayList<TagSetModel> getmTagsSetsList() {
        return mTagsSetsList;
    }

    public void setmTagsSetsList(ArrayList<TagSetModel> mTagsSetsList) {
        this.mTagsSetsList = mTagsSetsList;
    }
}