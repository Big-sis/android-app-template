package fr.wildcodeschool.vyfe.repository;

import com.google.firebase.database.FirebaseDatabase;

import fr.wildcodeschool.vyfe.Constants;
import fr.wildcodeschool.vyfe.model.TagSetModel;

public class SimpleTagSetRepository extends FirebaseDatabaseRepository<TagSetModel>{
    String userId;
    String tagSetId;

    public SimpleTagSetRepository(String userId, String tagSetId) {
        super(new TagSetMapper());
        this.userId = userId;
        this.tagSetId = tagSetId;
        databaseReference = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL).getReference(getRootNode());
    }

    @Override
    protected String getRootNode() {
        return "NomEntreprise/Users/"+this.userId+"/TagSets/"+this.tagSetId;
    }

}
