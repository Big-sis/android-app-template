package fr.wildcodeschool.vyfe.model;


public class VimeoTokenModel  {
    public VimeoTokenModel(){}

    public String getVimeoAccessToken() {
        return vimeoAccessToken;
    }

    public void setVimeoAccessToken(String vimeoAccessToken) {
        this.vimeoAccessToken = vimeoAccessToken;
    }

    private String vimeoAccessToken;
}
