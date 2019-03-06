package fr.vyfe.entity;

import java.util.HashMap;
import java.util.Map;

public class TemplateEntity {
    private String color;
    private String name;
    private int leftOffset;
    private int rightOffset;
    private int position;
    private boolean draft;
    private Integer count;

    public Map<String, Object> toHashmap() {
        Map<String, Object> map = new HashMap<>();
        map.put("count", count);
        return map;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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

    public int getLeftOffset() {
        return leftOffset;
    }

    public void setLeftOffset(int leftOffset) {
        this.leftOffset = leftOffset;
    }

    public int getRightOffset() {
        return rightOffset;
    }

    public void setRightOffset(int rightOffset) {
        this.rightOffset = rightOffset;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
