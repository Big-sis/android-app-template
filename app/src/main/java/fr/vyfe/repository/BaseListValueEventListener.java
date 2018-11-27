package fr.vyfe.repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import fr.vyfe.mapper.FirebaseMapper;
import fr.vyfe.model.VyfeModel;

public class BaseListValueEventListener<Model extends VyfeModel, Entity> implements ValueEventListener {

    private FirebaseMapper<Entity, Model> mapper;
    private CallbackInterface<Model> callback;

    public BaseListValueEventListener(FirebaseMapper<Entity, Model> mapper,
                                      CallbackInterface<Model> callback) {
        this.mapper = mapper;
        this.callback = callback;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        List<Model> data = mapper.mapList(dataSnapshot);
        callback.onSuccess(data);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        callback.onError(databaseError.toException());
    }

    public interface CallbackInterface<T> {
        void onSuccess(List<T> result);


        void onError(Exception e);
    }
}
