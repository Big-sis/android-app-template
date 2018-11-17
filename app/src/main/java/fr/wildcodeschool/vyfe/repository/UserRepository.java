package fr.wildcodeschool.vyfe.repository;

import fr.wildcodeschool.vyfe.model.SessionModel;


public class UserRepository extends FirebaseDatabaseRepository<SessionModel> {

    public UserRepository(String companyId) {
        super(new SessionMapper(), companyId);
    }

    @Override
    protected String getRootNode() {
        return getCompany() + "/Users";
    }

}
