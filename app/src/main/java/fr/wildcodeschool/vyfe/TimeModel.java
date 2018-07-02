package fr.wildcodeschool.vyfe;

public class TimeModel {
    private int start;
    private int end;

    public TimeModel(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public TimeModel() {
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
