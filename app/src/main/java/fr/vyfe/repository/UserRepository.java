package fr.vyfe.repository;

import fr.vyfe.Constants;
import fr.vyfe.mapper.SessionMapper;
import fr.vyfe.model.SessionModel;


public class UserRepository extends FirebaseDatabaseRepository<SessionModel> {

    public UserRepository(String companyId) {
        super(new SessionMapper(), companyId);
    }

    @Override
    protected String getRootNode() {
        return getCompany() + "/" + Constants.BDDV2_USERS_KEY;
    }

}
