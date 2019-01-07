package fr.vyfe.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import fr.vyfe.Constants;
import fr.vyfe.entity.SessionEntity;
import fr.vyfe.mapper.FirebaseMapper;
import fr.vyfe.model.SessionModel;

public abstract class FirebaseDatabaseRepository<Model> {

    // databaserefence is supposed to be private but we need access it in TagSetRepository since the
    // database is not perfectly architectured
    DatabaseReference databaseReference;
    private BaseListValueEventListener listListener;
    private BaseSingleValueEventListener childListener;
    private FirebaseMapper mapper;
    private String company;
    private String user;
    private String session;
    private String tagSetId;
    private String orderByChildKey;
    private String equalToKey;
    private String childKey;
    private boolean archived;

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
        databaseReference = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL).getReference(getRootNode());
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

    public void setEqualToKeyBoolean(Boolean archived) {
        this.archived = archived;
    }

    protected abstract String getRootNode();

    public void addListListener(BaseListValueEventListener.CallbackInterface<Model> callback) {
        listListener = new BaseListValueEventListener(mapper, callback);
        Query query = databaseReference;
        if (orderByChildKey != null) query = query.orderByChild(orderByChildKey);
        if (equalToKey != null) query = query.equalTo(equalToKey);
        query.addValueEventListener(listListener);
    }

    public void addListListenerBoolean(BaseListValueEventListener.CallbackInterface<Model> callback) {
        listListener = new BaseListValueEventListener(mapper, callback);
        Query query = databaseReference;
        if (orderByChildKey != null) query = query.orderByChild(orderByChildKey);
        if (archived != true) query = query.equalTo(archived);
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

    public void patch(String entityKey, HashMap<String, Object> properties) {
        for (Map.Entry<String, Object> property : properties.entrySet()) {
            databaseReference.child(entityKey).child(property.getKey()).setValue(property.getValue());
        }
    }

    public Task<Void> put(SessionModel sessionModel) {
        SessionEntity sessionEntityTest = (SessionEntity) mapper.unMap(sessionModel);
        return databaseReference.child(sessionModel.getId()).setValue(sessionEntityTest);
    }
}