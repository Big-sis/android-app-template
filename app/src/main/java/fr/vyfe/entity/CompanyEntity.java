package fr.vyfe.entity;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class CompanyEntity {

    public CompanyEntity(){}

    public String getVimeoAccessToken() {
        return vimeoAccessToken;
    }

    public void setVimeoAccessToken(String vimeoAccessToken) {
        this.vimeoAccessToken = vimeoAccessToken;
    }

    private String vimeoAccessToken;







}
