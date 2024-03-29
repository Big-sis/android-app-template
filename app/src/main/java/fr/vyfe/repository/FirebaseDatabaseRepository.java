package fr.vyfe.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import fr.vyfe.BuildConfig;
import fr.vyfe.Constants;
import fr.vyfe.mapper.FirebaseMapper;
import fr.vyfe.model.SessionModel;

public abstract class FirebaseDatabaseRepository<Model> {

    // databaserefence is supposed to be private but we need access it in TagSetRepository since the
    // database is not perfectly architectured
    DatabaseReference databaseReference;
    BaseListValueEventListener listListener;
    private BaseSingleValueEventListener childListener;
    protected FirebaseMapper mapper;
    private String company;
    private String user;
    private String session;
    private String tagSetId;
    private String orderByChildKey;
    private String equalToKey;
    private String childKey;
    static boolean isPersistenceEnabled = false;

    public FirebaseDatabaseRepository(FirebaseMapper mapper, String company) {
        this(mapper, company, null, null);
    }

    public FirebaseDatabaseRepository(FirebaseMapper mapper, String company, String user) {
        this(mapper, company, user, null, null);
    }

    public FirebaseDatabaseRepository(FirebaseMapper mapper, String company, String user, String session) {
        this(mapper, company, user, session, null);
    }

    public FirebaseDatabaseRepository(FirebaseMapper mapper, String company, String user, String session, String tagSetId) {
        this.mapper = mapper;
        this.company = company;
        this.user = user;
        this.session = session;
        this.tagSetId = tagSetId;
        if (!isPersistenceEnabled)
        {
            FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).setPersistenceEnabled(true);
            isPersistenceEnabled = true;
        }
        databaseReference = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference(getRootNode());
        databaseReference.keepSynced(true);

    }


    protected String getCompany() {
        return this.company;
    }

    protected String getUser() {
        return this.user;
    }

    protected String getSession() {
        return session;
    }

    protected String getTagSetId() {
        return tagSetId;
    }

    public void setOrderByChildKey(String orderByChildKey) {
        this.orderByChildKey = orderByChildKey;
    }

    public void setEqualToKey(String equalToKey) {
        this.equalToKey = equalToKey;
    }

    protected abstract String getRootNode();

    public void addListListener(BaseListValueEventListener.CallbackInterface<Model> callback) {
        listListener = new BaseListValueEventListener(mapper, callback);
        Query query = databaseReference;
        if (orderByChildKey != null) query = query.orderByChild(orderByChildKey);
        if (equalToKey != null) query = query.equalTo(equalToKey);
        query.addValueEventListener(listListener);
    }


    public void addChildListener(String childId, BaseSingleValueEventListener.CallbackInterface<Model> callback) {
        this.addChildListener(childId, false, callback);
    }

    public void addChildListener(String childId, boolean singleValueEvent, BaseSingleValueEventListener.CallbackInterface<Model> callback) {
        this.childKey = childId;
        childListener = new BaseSingleValueEventListener(mapper, callback);
        if (singleValueEvent)
            databaseReference.child(childId).addListenerForSingleValueEvent(childListener);
        else
            databaseReference.child(childId).addValueEventListener(childListener);
    }

    public void removeListeners() {
        if (listListener != null)
            databaseReference.removeEventListener(listListener);
        if (childListener != null)
            databaseReference.child(childKey).removeEventListener(childListener);
    }

    public String push(Model model) {
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(mapper.unMap(model));
        return key;
    }

    public Task<Void> remove(String key) {
        if (childListener != null)
            databaseReference.child(key).removeEventListener(childListener);
        return databaseReference.child(key).removeValue();
    }

    public void update(String entityKey, HashMap<String, Object> properties) {
        for (Map.Entry<String, Object> property : properties.entrySet()) {
            databaseReference.child(entityKey).child(property.getKey()).setValue(property.getValue());
        }
    }

    public Task<Void> put(SessionModel sessionModel) {
        return databaseReference.child(sessionModel.getId()).setValue(mapper.unMap(sessionModel));
    }


}