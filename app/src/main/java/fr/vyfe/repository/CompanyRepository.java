package fr.vyfe.repository;


import com.google.firebase.database.FirebaseDatabase;

import fr.vyfe.Constants;
import fr.vyfe.model.CompanyModel;

public class CompanyRepository extends FirebaseDatabaseRepository<CompanyModel> {

    public CompanyRepository(String companyId) {
        super(new CompanyMapper(), companyId);
        databaseReference = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL).getReference(getRootNode());
    }

    @Override
    protected String getRootNode() {
        return getCompany() + '/';
    }

}
