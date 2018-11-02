package fr.wildcodeschool.vyfe.repository;



import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import fr.wildcodeschool.vyfe.Constants;
import fr.wildcodeschool.vyfe.model.SessionModel;


public class AllUserSessionsRepository extends FirebaseDatabaseRepositorySingle<SessionModel>{
    private String userId;
    private  DatabaseReference databaseReference;

    public AllUserSessionsRepository(String userId) {
        super(new SessionMapper());
        this.userId = userId;
        databaseReference = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL).getReference(getRootNode());
        databaseReference.keepSynced(true);
        databaseReference.orderByChild("author").equalTo(userId);

    }

    //TODO: changer chemin acces + mettre filtre sur les tagger
   // @Override
    protected String getRootNode() {
        return "NomEntreprise/Sessions/";
    }
}
