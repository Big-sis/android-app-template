package fr.vyfe.entity;

public class TemplateEntity {
    private String color;
    private String name;
    private int leftOffset;
    private int rigthOffset;


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

    public int getLeftOffset() {
        return leftOffset;
    }

    public void setLeftOffset(int leftOffset) {
        this.leftOffset = leftOffset;
    }

    public int getRigthOffset() {
        return rigthOffset;
    }

    public void setRigthOffset(int rigthOffset) {
        this.rigthOffset = rigthOffset;
    }
}
