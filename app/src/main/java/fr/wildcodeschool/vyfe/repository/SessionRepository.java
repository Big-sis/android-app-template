package fr.wildcodeschool.vyfe.repository;


import com.google.firebase.database.FirebaseDatabase;

import fr.wildcodeschool.vyfe.Constants;
import fr.wildcodeschool.vyfe.model.SessionModel;

public class SessionRepository extends FirebaseDatabaseRepositorySingle<SessionModel> {

    private String userId;
    private String sessionId;

    public SessionRepository(String userId, String sessionId) {
        super(new SessionMapper());
        this.userId = userId;
        this.sessionId = sessionId;
        databaseReference = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL).getReference(getRootNode());

    }

    @Override
    protected String getRootNode() {
        return this.userId + "/sessions/" + this.sessionId;
    }

}
