package fr.vyfe.repository;

import fr.vyfe.mapper.TagMapper;
import fr.vyfe.model.TagModel;


public class TagRepository extends FirebaseDatabaseRepository<TagModel>{

    public TagRepository(String companyId, String userId, String sessionId) {
        super(new TagMapper(), companyId, userId, sessionId);
    }

    @Override
    protected String getRootNode() {
        return getCompany() + "/Sessions/" + this.getSession() + "/Tags/";
    }
}
