package fr.vyfe.model;

public class LicenceModel {
    String idLicence;
    String end;
    String start;
    String idUser;

    public LicenceModel(String idLicence, String end, String start, String userId) {
        this.idLicence = idLicence;
        this.end = end;
        this.start = start;
        this.idUser = userId;
    }

    public LicenceModel() {
    }

    public String getIdLicence() {
        return idLicence;
    }

    public void setIdLicence(String idLicence) {
        this.idLicence = idLicence;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getUserId() {
        return idUser;
    }

    public void setUserId(String userId) {
        this.idUser = userId;
    }
}
