package fr.wildcodeschool.vyfe;

import java.util.ArrayList;

public class SingletonTags {


    public static SingletonTags sInstance = null;


    private ArrayList<TagModel> mTagsList = new ArrayList<>();

    private SingletonTags() {
    }

    public static SingletonTags getInstance() {
        if (sInstance == null) {
            sInstance = new SingletonTags();
        }
        return sInstance;
    }

    public ArrayList<TagModel> getmTagsList() {
        return mTagsList;
    }

    public void setmTagsList(ArrayList<TagModel> mTagsList) {
        this.mTagsList = mTagsList;
    }
}


