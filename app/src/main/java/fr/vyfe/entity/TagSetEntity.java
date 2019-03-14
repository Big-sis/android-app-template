package fr.vyfe.entity;

import java.util.HashMap;

public class TagSetEntity {
    private String name;
    private String owner;
    private boolean shared;
    private HashMap<String, TemplateEntity> Templates;


    public HashMap<String, TemplateEntity> getTemplates() {
        return Templates;
    }

    public void setTemplates(HashMap<String, TemplateEntity> templates) {
        this.Templates = templates;
    }



    //Generate code


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }
}
