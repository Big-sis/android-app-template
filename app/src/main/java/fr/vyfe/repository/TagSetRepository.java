package fr.vyfe.repository;

import fr.vyfe.model.TagSetModel;


public class TagSetRepository extends FirebaseDatabaseRepository<TagSetModel>{
    String userId;

    public TagSetRepository(String userId, String companyId) {
        super(new TagSetMapper(), companyId);
        this.userId = userId;
    }

    @Override
    protected String getRootNode() {
        return getCompany() + "/Users/" + this.userId + "/TagSets/";
    }

}
