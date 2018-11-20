package fr.wildcodeschool.vyfe.entity;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class VimeoTokenEntity {

    public VimeoTokenEntity(){}

    public String getVimeoAccessToken() {
        return vimeoAccessToken;
    }

    public void setVimeoAccessToken(String vimeoAccessToken) {
        this.vimeoAccessToken = vimeoAccessToken;
    }

    private String vimeoAccessToken;







}
