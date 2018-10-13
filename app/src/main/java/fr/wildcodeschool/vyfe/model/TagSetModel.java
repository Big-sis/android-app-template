package fr.wildcodeschool.vyfe.model;

public class TagSetModel {
    private String id;
    private String name;

    public TagSetModel() {}

    public TagSetModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
