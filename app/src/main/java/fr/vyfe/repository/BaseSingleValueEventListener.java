package fr.vyfe.repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class BaseSingleValueEventListener<Entity, Model> implements ValueEventListener {

    private FirebaseMapper<Entity, Model> mapper;
    private CallbackInterface<Model> callback;

    public BaseSingleValueEventListener(FirebaseMapper<Entity, Model> mapper,
                                        CallbackInterface<Model> callback) {
        this.mapper = mapper;
        this.callback = callback;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Model data = mapper.map(dataSnapshot,"");
        callback.onSuccess(data);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        callback.onError(databaseError.toException());
    }

    public interface CallbackInterface<T> {
        void onSuccess(T result);


        void onError(Exception e);
    }
}
