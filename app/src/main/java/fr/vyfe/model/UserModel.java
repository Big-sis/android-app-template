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
    private String[] roles;
    private Timestamp licenceEnd;
    private HashMap<String, Boolean> rolesC;
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

    public Date getLicenceEnd() {
        return licenceEnd;
    }

    public HashMap<String, Boolean> getRolesC() {
        return rolesC;
    }

    public void setRolesC(HashMap<String, Boolean> rolesC) {
        this.rolesC = rolesC;
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

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public Timestamp getLicenseEnd() {
        return licenceEnd;
    }

    public void setLicenceEnd(Timestamp date){
        this.licenceEnd = date;
    }
}
