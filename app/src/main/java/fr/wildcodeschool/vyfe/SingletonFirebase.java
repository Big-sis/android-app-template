package fr.wildcodeschool.vyfe;

import com.google.firebase.database.FirebaseDatabase;

public class SingletonFirebase {

    private static SingletonFirebase sInstance = null;

    private FirebaseDatabase mDatabase;
    private String mUid;

    private SingletonFirebase() {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.setPersistenceEnabled(true);
    }

    public static SingletonFirebase getInstance() {
        if (sInstance == null) {
            sInstance = new SingletonFirebase();

        }
        return sInstance;
    }

    public static void setsInstance(SingletonFirebase sInstance) {
        SingletonFirebase.sInstance = sInstance;
    }

    public void setmDatabase(FirebaseDatabase mDatabase) {
        this.mDatabase = mDatabase;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public FirebaseDatabase getDatabase() {
        return mDatabase;
    }

    public void logUser(String uId) {
        this.mUid = uId;
    }

    public String getUid() {
        return mUid;
    }
}
