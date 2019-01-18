package fr.vyfe.repository;

import fr.vyfe.Constants;
import fr.vyfe.mapper.TagMapper;
import fr.vyfe.model.TagModel;


public class TagRepository extends FirebaseDatabaseRepository<TagModel> {

    public TagRepository(String companyId, String userId, String sessionId) {
        super(new TagMapper(), companyId, userId, sessionId);
    }

    @Override
    protected String getRootNode() {
        return getCompany() + "/" + Constants.BDDV2_SESSIONS_KEY + "/" + this.getSession() + "/" + Constants.BDDV2_USERS_TAGS_KEY + "/";
    }
}
