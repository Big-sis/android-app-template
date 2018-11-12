package fr.wildcodeschool.vyfe.repository;


import com.google.firebase.database.FirebaseDatabase;
import fr.wildcodeschool.vyfe.Constants;
import fr.wildcodeschool.vyfe.model.SessionModel;

public class UserSessionRepository extends FirebaseDatabaseRepository<SessionModel> {

    private String userId;

    public UserSessionRepository(String userId) {
        super(new SessionMapper());
        this.userId = userId;
        databaseReference = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL).getReference(getRootNode()).orderByChild("author").equalTo(userId);
    }


    @Override
    protected String getRootNode() {
        return "NomEntreprise/Sessions/";
    }

}
