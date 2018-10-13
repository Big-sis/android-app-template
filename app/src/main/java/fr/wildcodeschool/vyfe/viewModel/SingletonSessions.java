package fr.wildcodeschool.vyfe.viewModel;

import java.util.ArrayList;

import fr.wildcodeschool.vyfe.model.SessionModel;


//TODO Revoir le passage de paramètres entre les activités MyVideo et PLayVideo
public class SingletonSessions {
    public static SingletonSessions sInstance = null;

    private ArrayList<SessionModel> mSessionsList = new ArrayList<>();
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

    public ArrayList<SessionModel> getmSessionsList() {
        return mSessionsList;
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
