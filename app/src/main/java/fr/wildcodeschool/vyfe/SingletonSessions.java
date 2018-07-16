package fr.wildcodeschool.vyfe;

import java.util.ArrayList;

public class SingletonSessions {
    public static SingletonSessions sInstance = null;


    private ArrayList<SessionsModel> mSessionsList = new ArrayList<>();
    private String idSession;
    private String titleSession;
    private String fileName;

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

    public String getIdSession() {
        return idSession;
    }

    public void setIdSession(String idSession) {
        this.idSession = idSession;
    }

    public String getTitleSession() {
        return titleSession;
    }

    public void setTitleSession(String titleSession) {
        this.titleSession = titleSession;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
