package fr.vyfe.model;


public class CompanyModel implements VyfeModel{

    private String id;
    private String vimeoAccessToken;


    public String getVimeoAccessToken() {
        return vimeoAccessToken;
    }

    public void setVimeoAccessToken(String vimeoAccessToken) {
        this.vimeoAccessToken = vimeoAccessToken;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
