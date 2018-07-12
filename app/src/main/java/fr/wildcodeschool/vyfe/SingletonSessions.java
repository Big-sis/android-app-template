package fr.wildcodeschool.vyfe;

import java.util.ArrayList;

public class SingletonSessions {
    public static SingletonSessions sInstance = null;

    private ArrayList<SessionsModel> mSessionsList = new ArrayList<>();

    private SingletonSessions() {
    }

    public static SingletonSessions getInstance() {
        if (sInstance == null) {
            sInstance = new SingletonSessions();
        }
        return sInstance;
    }

    public ArrayList<SessionsModel> getmSessionsList() {
        return mSessionsList;
    }

    public void setmSessionsList(ArrayList<SessionsModel> mSessionsList) {
        this.mSessionsList = mSessionsList;
    }

}
