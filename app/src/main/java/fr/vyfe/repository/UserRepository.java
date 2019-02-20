package fr.vyfe.repository;

import fr.vyfe.Constants;
import fr.vyfe.mapper.SessionMapper;
import fr.vyfe.mapper.UserMapper;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.UserModel;


public class UserRepository extends FirebaseDatabaseRepository<UserModel> {

    public UserRepository(String company, String IdUser) {
        super(new UserMapper(), company, IdUser);
    }

    @Override
    protected String getRootNode() {
        return getCompany() + "/" + Constants.BDDV2_USERS_KEY+"/";
    }

}
