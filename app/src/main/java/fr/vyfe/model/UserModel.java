package fr.vyfe.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

public class UserModel implements VyfeModel {
    private String id;
    private String firstname;
    private String lastName;
    private String promo;
    private String company;
    private Timestamp licenceEnd;
    private HashMap<String, Boolean> roles;
    private String vimeoAccessToken;

    public UserModel(String id) {
        this.id = id;
    }

    public UserModel() {    }

    public String getVimeoAccessToken() {
        return vimeoAccessToken;
    }

    public void setVimeoAccessToken(String vimeoAccessToken) {
        this.vimeoAccessToken = vimeoAccessToken;
    }


    public HashMap<String, Boolean> getRoles() {
        return roles;
    }

    public void setRoles(HashMap<String, Boolean> roles) {
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPromo() {
        return promo;
    }

    public void setPromo(String promo) {
        this.promo = promo;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Timestamp getLicenseEnd() {
        return licenceEnd;
    }

    public void setLicenceEnd(Timestamp date){
        this.licenceEnd = date;
    }
}
