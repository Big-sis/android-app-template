package fr.vyfe.repository;


import com.google.firebase.database.FirebaseDatabase;

import fr.vyfe.BuildConfig;
import fr.vyfe.Constants;
import fr.vyfe.model.CompanyModel;

public class CompanyRepository extends FirebaseDatabaseRepository<CompanyModel> {

    public CompanyRepository(String companyId) {
        super(new CompanyMapper(), companyId);
        databaseReference = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference(getRootNode());
    }

    @Override
    protected String getRootNode() {
        return getCompany() + '/';
    }

}
