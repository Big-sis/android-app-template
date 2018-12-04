package fr.vyfe.entity;

import java.util.HashMap;

public class TagSetEntity {
    private String name;

    private HashMap<String, TemplateEntity> Templates;


    public HashMap<String, TemplateEntity> getTemplates() {
        return Templates;
    }

    public void setTemplates(HashMap<String, TemplateEntity> templates) {
        this.Templates = templates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
