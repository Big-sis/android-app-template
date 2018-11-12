package fr.wildcodeschool.vyfe.repository;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

import fr.wildcodeschool.vyfe.Constants;

public abstract class FirebaseDatabaseRepository<Model> {

    protected Query databaseReference;
    protected CallbackInterface<Model> firebaseCallback;
    private BaseListValueEventListener listener;
    private FirebaseMapper mapper;

    public FirebaseDatabaseRepository(FirebaseMapper mapper) {
        databaseReference = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL).getReference(getRootNode());
        databaseReference.keepSynced(true);
        this.mapper = mapper;
    }

    protected abstract String getRootNode();

    public void addListener(CallbackInterface<Model> callback) {
        this.firebaseCallback = callback;
        listener = new BaseListValueEventListener(mapper, callback);
        databaseReference.addValueEventListener(listener);
    }


    public void removeListener() {
        if (listener != null)
            databaseReference.removeEventListener(listener);
    }

    public interface CallbackInterface<T> {
        void onSuccess(List<T> result);

        void onError(Exception e);
    }

}