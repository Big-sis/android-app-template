package fr.wildcodeschool.vyfe;

public class TagModel {

    private int color;
    private String name;
    private String fkTagSet;
    private String rigthOffset;
    private int time = 0;

    public TagModel(int color, String name, String fkTagSet, String rigthOffset) {
        this.color = color;
        this.name = name;
        this.fkTagSet = fkTagSet;
        this.rigthOffset = rigthOffset;
    }

    public TagModel() {
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFkTagSet() {
        return fkTagSet;
    }

    public void setFkTagSet(String fkTagSet) {
        this.fkTagSet = fkTagSet;
    }

    public String getRigthOffset() {
        return rigthOffset;
    }

    public void setRigthOffset(String rigthOffset) {
        this.rigthOffset = rigthOffset;
    }
}


