package fr.vyfe.entity;

import java.util.HashMap;

public class UserEntity {
    private String firstName;
    private String lastName;
    private boolean confirmed;
    private HashMap<String,Boolean> sessions;


  //Generate code

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public HashMap<String, Boolean> getSessions() {
        return sessions;
    }

    public void setSessions(HashMap<String, Boolean> sessions) {
        this.sessions = sessions;
    }
}
