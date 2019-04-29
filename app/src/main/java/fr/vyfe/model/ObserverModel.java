package fr.vyfe.model;



public class ObserverModel implements VyfeModel {
    private String name;
    private String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id= id;

    }

    public ObserverModel(String uid, String displayName) {
        this.id = uid;
        this.name = displayName;
    }

    public ObserverModel() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
