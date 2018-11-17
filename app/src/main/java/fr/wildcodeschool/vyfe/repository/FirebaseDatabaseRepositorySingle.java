package fr.wildcodeschool.vyfe.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public abstract class FirebaseDatabaseRepositorySingle<Model> {

    protected Query databaseReference;
    protected BaseListValueEventListener.CallbackInterface<Model> firebaseCallback;
    private BaseListValueEventListener listener;
    private FirebaseMapper mapper;

    protected abstract String getRootNode();

    public FirebaseDatabaseRepositorySingle(FirebaseMapper mapper) {
        this.mapper = mapper;
    }

    public void addListener(BaseListValueEventListener.CallbackInterface<Model> callback) {
        this.firebaseCallback = callback;
        listener = new BaseListValueEventListener(mapper, callback);
        databaseReference.addValueEventListener(listener);
    }


    public void removeListener() {
        databaseReference.removeEventListener(listener);
    }



}