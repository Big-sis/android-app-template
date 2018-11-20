package fr.wildcodeschool.vyfe.repository;


import com.google.firebase.database.FirebaseDatabase;

import fr.wildcodeschool.vyfe.Constants;
import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.model.VimeoTokenModel;

public class VimeoTokenRepository extends FirebaseDatabaseRepositorySingle<VimeoTokenModel> {

    public VimeoTokenRepository() {
        super(new VimeoTokenMapper());
        databaseReference = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL).getReference(getRootNode());
    }

    @Override                       //TODO: mettre getCompagny
    protected String getRootNode() {
        return "NomEntreprise/";
    }

}
