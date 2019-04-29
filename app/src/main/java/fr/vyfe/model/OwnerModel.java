package fr.vyfe.model;

public class OwnerModel {
    private String uid;
    private String diplayName;

    public OwnerModel(String uid, String displayName) {
        this.uid = uid;
        this.diplayName = displayName;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDiplayName() {
        return diplayName;
    }

    public void setDiplayName(String diplayName) {
        this.diplayName = diplayName;
    }
}
