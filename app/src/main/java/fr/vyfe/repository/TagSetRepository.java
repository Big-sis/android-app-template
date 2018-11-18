package fr.vyfe.repository;

import java.util.List;

import fr.vyfe.mapper.TagMapper;
import fr.vyfe.mapper.TagSetMapper;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TagSetModel;


public class TagSetRepository extends FirebaseDatabaseRepository<TagSetModel>{

    public TagSetRepository(String userId, String companyId) {
        super(new TagSetMapper(), companyId, userId);
    }

    @Override
    protected String getRootNode() {
        return getCompany() + "/Users/" + this.getUser() + "/TagSets/";
    }

    // This methods has to work with BDD V2 strange architecture
    // It will be refactored with next BDD version
    public void createTags(String key, List<TagModel> tags){
        TagMapper mapper = new TagMapper();
        for (TagModel tagModel: tags) {
            databaseReference.child(key).child("Tags").push().setValue(mapper.unMap(tagModel));
        }
    }
}
