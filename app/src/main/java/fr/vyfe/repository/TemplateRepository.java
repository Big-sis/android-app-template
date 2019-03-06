package fr.vyfe.repository;

import fr.vyfe.Constants;
import fr.vyfe.mapper.TagMapper;
import fr.vyfe.model.TagModel;


public class TemplateRepository extends FirebaseDatabaseRepository<TagModel>{

    public TemplateRepository(String companyId, String userId, String tagSetId) {
        super(new TagMapper(), companyId, userId, null, tagSetId);
    }

    @Override
    protected String getRootNode() {
        return getCompany() + "/"+ Constants.BDDV2_USERS_KEY+"/" + this.getUser() + "/"+Constants.BDDV2_USERS_TAGSETS_KEY+"/" + this.getTagSetId() + "/"+Constants.BDDV2_USERS_TAGS_KEY;
    }
}
