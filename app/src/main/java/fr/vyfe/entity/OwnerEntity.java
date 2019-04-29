package fr.vyfe.entity;

public class OwnerEntity {
    private String uid;
    private String displayName;

    // generate code
    public OwnerEntity() {
    }

    public OwnerEntity(String uid, String displayName) {
        this.uid = uid;
        this.displayName = displayName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
