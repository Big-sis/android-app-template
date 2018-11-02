package fr.wildcodeschool.vyfe.repository;

import com.google.firebase.database.FirebaseDatabase;

import fr.wildcodeschool.vyfe.Constants;
import fr.wildcodeschool.vyfe.model.TagSetModel;

public class TagSetRepository extends FirebaseDatabaseRepository<TagSetModel>{
    String userId;

    public TagSetRepository(String userId) {
        super(new TagSetMapper());
        this.userId = userId;
        databaseReference = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL).getReference(getRootNode());
    }

    @Override
    protected String getRootNode() {
        return "NomEntreprise/Users/"+this.userId+"/TagSets/";
    }

}
