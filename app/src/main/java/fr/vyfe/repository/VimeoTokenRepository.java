package fr.vyfe.repository;


import com.google.firebase.database.FirebaseDatabase;

import fr.vyfe.Constants;
import fr.vyfe.model.VimeoTokenModel;

public class VimeoTokenRepository extends FirebaseDatabaseRepository<VimeoTokenModel> {

    public VimeoTokenRepository(String companyId) {
        super(new VimeoTokenMapper(), companyId);
        databaseReference = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL).getReference(getRootNode());
    }

    @Override
    protected String getRootNode() {
        return getCompany() + '/';
    }

}
