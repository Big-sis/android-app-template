package fr.wildcodeschool.vyfe.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public abstract class FirebaseDatabaseRepositorySingle<Model> {

    protected Query databaseReference;
    protected CallbackInterface<Model> firebaseCallback;
    private BaseSingleValueEventListener listener;
    private FirebaseMapper mapper;

    protected abstract String getRootNode();

    public FirebaseDatabaseRepositorySingle(FirebaseMapper mapper) {
        this.mapper = mapper;
    }

    public void addListener(CallbackInterface<Model> callback) {
        this.firebaseCallback = callback;
        listener = new BaseSingleValueEventListener(mapper, callback);
        databaseReference.addValueEventListener(listener);
    }


    public void removeListener() {
        databaseReference.removeEventListener(listener);
    }

    public interface CallbackInterface<T> {
        void onSuccess(T result);

        void onError(Exception e);
    }

}