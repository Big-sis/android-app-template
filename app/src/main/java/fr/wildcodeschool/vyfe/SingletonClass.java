package fr.wildcodeschool.vyfe;

import java.util.ArrayList;

public class SingletonClass {


    public static SingletonClass sInstance = null;


    private ArrayList<TagModel> mTagsList = new ArrayList<>();

    private SingletonClass() {
    }

    public static SingletonClass getInstance() {
        if (sInstance == null) {
            sInstance = new SingletonClass();
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


