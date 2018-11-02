package fr.wildcodeschool.vyfe.entity;

import java.util.HashMap;

public class TagSetEntity {
    private String name;

    private HashMap<String, TagEntity> Tags;


    public HashMap<String, TagEntity> getTags() {
        return Tags;
    }

    public void setTags(HashMap<String, TagEntity> tags) {
        Tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }






}
