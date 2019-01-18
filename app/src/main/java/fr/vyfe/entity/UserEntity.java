package fr.vyfe.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class UserEntity {
    private ProfilEntity profile;
    private AccountInformationsEntity accountInformations;
    private boolean confirmed;
    private String role[];
    private ArrayList<String> autorizeSessions;
    private HashMap<String,TagSetEntity> tagSets;



    public HashMap<String, TagSetEntity> getTagSets() {
        return tagSets;
    }

    public void setTagSets(HashMap<String, TagSetEntity> tagSets) {
        this.tagSets = tagSets;
    }


    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public ProfilEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfilEntity profile) {
        this.profile = profile;
    }

    public AccountInformationsEntity getAccountInformations() {
        return accountInformations;
    }

    public void setAccountInformations(AccountInformationsEntity accountInformations) {
        this.accountInformations = accountInformations;
    }


    public String[] getRole() {
        return role;
    }

    public void setRole(String[] role) {
        this.role = role;
    }

    public ArrayList<String> getAutorizeSessions() {
        return autorizeSessions;
    }

    public void setAutorizeSessions(ArrayList<String> autorizeSessions) {
        this.autorizeSessions = autorizeSessions;
    }


}
