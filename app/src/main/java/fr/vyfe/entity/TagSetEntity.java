package fr.vyfe.entity;

import java.util.HashMap;

public class TagSetEntity {
    private String name;

    private HashMap<String, TemplateEntity> Tags;


    public HashMap<String, TemplateEntity> getTags() {
        return Tags;
    }

    public void setTags(HashMap<String, TemplateEntity> tags) {
        this.Tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
