package fr.vyfe.entity;

public class TagEntity {
    private String color;
    private String name;
    private String templateId;
    private String taggerId;
    private int start;
    private int end;
    private OwnerEntity author;

    public OwnerEntity getAuthor() {
        return author;
    }

    public void setAuthor(OwnerEntity author) {
        this.author = author;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTaggerId() {
        return taggerId;
    }

    public void setTaggerId(String taggerId) {
        this.taggerId = taggerId;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
