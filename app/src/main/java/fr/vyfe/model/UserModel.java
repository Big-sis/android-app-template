package fr.vyfe.model;

import java.util.Date;

public class UserModel implements VyfeModel {
    private String id;
    private String firstname;
    private String lastName;
    private String promo;
    private String company;
    private String[] roles;
    private Date licenceEnd;


    public UserModel(String id) {
        this.id = id;
    }

    public UserModel() {    }


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

    public Date getLicenseEnd() {
        return licenceEnd;
    }

    public void setLicenceEnd(Date date){
        this.licenceEnd = date;
    }
}
