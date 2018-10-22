package fr.wildcodeschool.vyfe.repository;


import com.google.firebase.database.FirebaseDatabase;

import fr.wildcodeschool.vyfe.Constants;
import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.model.SessionModelBDD2;

public class SessionRepositoryBDD2 extends FirebaseDatabaseRepositorySingle<SessionModelBDD2> {

    private String userId;
    private String sessionId;

    public SessionRepositoryBDD2(String userId, String sessionId) {
        super(new SessionMapperBDD2());
        this.userId = userId;
        this.sessionId = sessionId;
        databaseReference = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URLBDD2).getReference(getRootNode());

    }

    @Override
    protected String getRootNode() {
        return this.userId + "/sessions/" + this.sessionId;
    }

}
