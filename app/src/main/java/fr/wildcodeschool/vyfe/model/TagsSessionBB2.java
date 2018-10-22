package fr.wildcodeschool.vyfe.model;

public class TagsSessionBB2{
    private String idTag;
    private String color;
    private String name;
    private TimesTaggerSessionBB2 taggerIds;
    private int count = 0;

    public TagsSessionBB2(String idTag, String color, String name, TimesTaggerSessionBB2 taggerIds) {
        this.idTag = idTag;
        this.color = color;
        this.name = name;
        this.taggerIds = taggerIds;
    }

    public String getIdTag() {
        return idTag;
    }

    public void setIdTag(String idTag) {
        this.idTag = idTag;
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

    public TimesTaggerSessionBB2 getTaggerIds() {
        return taggerIds;
    }

    public void setTaggerIds(TimesTaggerSessionBB2 taggerIds) {
        this.taggerIds = taggerIds;
    }
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
