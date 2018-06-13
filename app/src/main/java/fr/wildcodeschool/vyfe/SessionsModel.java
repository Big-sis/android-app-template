package fr.wildcodeschool.vyfe;

public class SessionsModel {
    private String name;
    private String author;
    private String fkGroup;
    private String videoLink;
    private String date;

    public SessionsModel( String name, String author, String fkGroup, String videoLink, String date) {

        this.name = name;
        this.author = author;
        this.fkGroup = fkGroup;
        this.videoLink = videoLink;
        this.date = date;
    }
    public SessionsModel(){}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFkGroup() {
        return fkGroup;
    }

    public void setFkGroup(String fkGroup) {
        this.fkGroup = fkGroup;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
