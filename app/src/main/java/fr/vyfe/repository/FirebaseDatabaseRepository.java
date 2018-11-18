package fr.vyfe.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import fr.vyfe.Constants;
import fr.vyfe.mapper.FirebaseMapper;

public abstract class FirebaseDatabaseRepository<Model> {

    // databaserefence is supposed to e private but we need access it in TagSetRepository since the
    // database is not perfectly architectured
    DatabaseReference databaseReference;
    private BaseListValueEventListener listListener;
    private BaseSingleValueEventListener childListener;
    private FirebaseMapper mapper;
    private String company;
    private String user;

    public FirebaseDatabaseRepository(FirebaseMapper mapper, String company) {
        this.mapper = mapper;
        this.company = company;
        databaseReference = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL).getReference(getRootNode());
        databaseReference.keepSynced(true);
    }

    public FirebaseDatabaseRepository(FirebaseMapper mapper, String company, String user) {
        this.mapper = mapper;
        this.company = company;
        this.user = user;
        databaseReference = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL).getReference(getRootNode());
        databaseReference.keepSynced(true);
    }


    protected String getCompany() {
        return this.company;
    }

    protected String getUser() {
        return this.user;
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

    public String push(Model model) {
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(mapper.unMap(model));
        return key;
    }

}