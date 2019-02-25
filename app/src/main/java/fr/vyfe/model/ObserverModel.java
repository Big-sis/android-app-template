package fr.vyfe.model;



public class ObserverModel implements VyfeModel {
    private String idObserver;
    private String nameObserver;

    public String getIdObserver() {
        return idObserver;
    }

    public void setIdObserver(String idObserver) {
        this.idObserver = idObserver;
    }

    public String getNameObserver() {
        return nameObserver;
    }

    public void setNameObserver(String nameObserver) {
        this.nameObserver = nameObserver;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void setId(String id) {

    }
}
