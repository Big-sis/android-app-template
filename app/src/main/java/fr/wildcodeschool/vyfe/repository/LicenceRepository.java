package fr.wildcodeschool.vyfe.repository;


import com.google.firebase.database.FirebaseDatabase;

import fr.wildcodeschool.vyfe.Constants;
import fr.wildcodeschool.vyfe.model.LicenceModel;

public class LicenceRepository extends  FirebaseDatabaseRepository<LicenceModel> {

    private String idUser;

    public LicenceRepository(String idUser) {
        super(new LicenseMapper());
        this.idUser = idUser;
        databaseReference = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL).getReference(getRootNode()).orderByChild("idUser").equalTo(idUser);
    }


    @Override
    protected String getRootNode() {
        return "NomEntreprise/Licenses/";
    }

}
