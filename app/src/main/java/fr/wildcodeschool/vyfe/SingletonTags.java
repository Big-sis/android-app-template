package fr.wildcodeschool.vyfe;

import java.util.ArrayList;

public class SingletonTags {


    public static SingletonTags sInstance = null;

    private int color;
    private String name;
    private String fkTagSet;
    private String rigthOffset;
    private ArrayList<TagModel> mTagsList = new ArrayList<>();

    public ArrayList<TagModel> getmTagsListAdd() {
        return mTagsListAdd;
    }

    public void setmTagsListAdd(ArrayList<TagModel> mTagsListAdd) {
        this.mTagsListAdd = mTagsListAdd;
    }

    private ArrayList<TagModel> mTagsListAdd = new ArrayList<>();

    private SingletonTags() {
    }

    public static SingletonTags getInstance() {
        if (sInstance == null) {
            sInstance = new SingletonTags();
        }
        return sInstance;
    }


    public static SingletonTags getsInstance() {
        return sInstance;
    }

    public static void setsInstance(SingletonTags sInstance) {
        SingletonTags.sInstance = sInstance;
    }

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

    public String getFkTagSet() {
        return fkTagSet;
    }

    public void setFkTagSet(String fkTagSet) {
        this.fkTagSet = fkTagSet;
    }

    public String getRigthOffset() {
        return rigthOffset;
    }

    public void setRigthOffset(String rigthOffset) {
        this.rigthOffset = rigthOffset;
    }

    public ArrayList<TagModel> getmTagsList() {
        return mTagsList;
    }

    public void setmTagsList(ArrayList<TagModel> mTagsList) {
        this.mTagsList = mTagsList;
    }
}


