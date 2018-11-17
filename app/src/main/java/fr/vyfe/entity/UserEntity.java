package fr.vyfe.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class UserEntity {
    private ProfilEntity Profil;
    private AccountInformationsEntity AccountInformations;
    private boolean confirmed;
    private String role[];
    private ArrayList<String> AutorizeSessions;
    private HashMap<String,TagSetEntity>TagSets;



    public HashMap<String, TagSetEntity> getTagSets() {
        return TagSets;
    }

    public void setTagSets(HashMap<String, TagSetEntity> tagSets) {
        TagSets = tagSets;
    }


    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public ProfilEntity getProfil() {
        return Profil;
    }

    public void setProfil(ProfilEntity profil) {
        Profil = profil;
    }

    public AccountInformationsEntity getAccountInformations() {
        return AccountInformations;
    }

    public void setAccountInformations(AccountInformationsEntity accountInformations) {
        AccountInformations = accountInformations;
    }


    public String[] getRole() {
        return role;
    }

    public void setRole(String[] role) {
        this.role = role;
    }

    public ArrayList<String> getAutorizeSessions() {
        return AutorizeSessions;
    }

    public void setAutorizeSessions(ArrayList<String> autorizeSessions) {
        AutorizeSessions = autorizeSessions;
    }


}
