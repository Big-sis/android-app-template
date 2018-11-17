package fr.wildcodeschool.vyfe.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import fr.wildcodeschool.vyfe.Constants;

public abstract class FirebaseDatabaseRepository<Model> {

    private DatabaseReference databaseReference;
    private BaseListValueEventListener listListener;
    private BaseSingleValueEventListener childListener;
    private FirebaseMapper mapper;
    private String company;

    public FirebaseDatabaseRepository(FirebaseMapper mapper, String company) {
        databaseReference = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL).getReference(getRootNode());
        databaseReference.keepSynced(true);
        this.mapper = mapper;
        this.company = company;
    }

    protected String getCompany() {
        return this.company;
    }

    protected abstract String getRootNode();

    public void addListListener(BaseListValueEventListener.CallbackInterface<Model> callback) {
        listListener = new BaseListValueEventListener(mapper, callback);
        databaseReference.addValueEventListener(listListener);
    }

    public void addChildListener(String childId, BaseSingleValueEventListener.CallbackInterface<Model> callback) {
        childListener = new BaseSingleValueEventListener(mapper, callback);
        databaseReference.child(childId).addValueEventListener(childListener);
    }

    public void removeListeners() {
        if (listListener != null)
            databaseReference.removeEventListener(listListener);
        if (childListener != null)
            databaseReference.removeEventListener(childListener);
    }

    public Task<Void> push(Model model) {
        return databaseReference.setValue(model);
    }


}