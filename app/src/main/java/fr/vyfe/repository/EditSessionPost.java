package fr.vyfe.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import fr.vyfe.Constants;
import fr.vyfe.model.SessionModel;

public class EditSessionPost {

    public static void sessionMapper(SessionModel sessionModel) {
        FirebaseDatabase mDatabase;
        mDatabase = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL);
        DatabaseReference sessionRef2 = mDatabase.getReference("NomEntreprise").child("Sessions").child(sessionModel.getIdSession());

        sessionRef2.child("name").setValue(sessionModel.getName());
        sessionRef2.child("description").setValue(sessionModel.getDescription());


    }

    public static void deleteSession (SessionModel sessionModel){
        FirebaseDatabase mDatabase;
        mDatabase = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL);
        DatabaseReference sessionRef2 = mDatabase.getReference("NomEntreprise").child("Sessions").child(sessionModel.getIdSession());

        sessionRef2.getRef().removeValue();
    }
}