package fr.vyfe.repository;

import fr.vyfe.mapper.TagMapper;
import fr.vyfe.model.TagModel;


public class TemplateRepository extends FirebaseDatabaseRepository<TagModel>{

    public TemplateRepository(String companyId, String userId, String tagSetId) {
        super(new TagMapper(), companyId, userId, null, tagSetId);
    }

    @Override
    protected String getRootNode() {
        return getCompany() + "/Users/" + this.getUser() + "/TagSets/" + this.getTagSetId() + "/Tags";
    }
}
