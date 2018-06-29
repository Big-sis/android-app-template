package fr.wildcodeschool.vyfe;

public class SessionsModel {
    private String name;
    private String author;
    private String videoLink;
    private String date;
    private String idSession;

    public SessionsModel(String name, String author, String videoLink, String date, String idSession) {
        this.name = name;
        this.author = author;
        this.videoLink = videoLink;
        this.date = date;
        this.idSession = idSession;
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

    public String getIdSession() {
        return idSession;
    }

    public void setIdSession(String idSession) {
        this.idSession = idSession;
    }
}
