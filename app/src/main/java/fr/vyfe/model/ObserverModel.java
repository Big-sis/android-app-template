package fr.vyfe.model;



public class ObserverModel implements VyfeModel {
    private String name;


    @Override
    public String getId() {
        return null;
    }

    @Override
    public void setId(String id) {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
