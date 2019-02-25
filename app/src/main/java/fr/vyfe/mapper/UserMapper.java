package fr.vyfe.mapper;


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import fr.vyfe.Constants;
import fr.vyfe.entity.UserEntity;
import fr.vyfe.model.UserModel;

public class UserMapper extends FirebaseMapper<UserEntity, UserModel> {

    @Override
    public UserModel map(UserEntity userEntity, String key) {
        UserModel userModel = new UserModel();
        if (userEntity != null) {
            if (userEntity.getFirstName() != null)
                userModel.setFirstname(userEntity.getFirstName());
            if (userEntity.getLastName() != null) userModel.setLastName(userEntity.getLastName());
            if(userEntity.getPromo() != null)userModel.setPromo(userEntity.getPromo());
        }
        return userModel;

    }

    @Override
    public UserEntity unMap(UserModel userModel) {
        return null;
    }

    @Override
    public HashMap<String, UserEntity> unMapList(List<UserModel> models) {
        return null;
    }

    public UserModel map(UserEntity userEntity, UserModel user) {
        return user;
    }

    /**
     * Map the result of getCompanyAndUser Firebase Function
     *
     * @param userMap
     * @return
     */

    public UserModel map(HashMap<String, Object> userMap) {
        UserModel user = new UserModel();
        HashMap<String, Boolean> hashMapRoles = new HashMap<>();
        for (String keyCustom : userMap.keySet()) {
            if (keyCustom.equals("user_id")) {
                user.setId(userMap.get(keyCustom).toString());
            }
            if (keyCustom.equals(Constants.BDDV2_CUSTOM_USERS_ROLE_TEACHER) || keyCustom.equals(Constants.BDDV2_CUSTOM_USERS_ROLE_OBSERVER) || keyCustom.equals(Constants.BDDV2_CUSTOM_USERS_ROLE_ADMIN) || keyCustom.equals(Constants.BDDV2_CUSTOM_USERS_ROLE_STUDENT)) {
                hashMapRoles.put(keyCustom, new Boolean(userMap.get(keyCustom).toString()));
            }

            if (keyCustom.equals(Constants.BDDV2_CUSTOM_USERS_VIMEOACCESSTOKEN)) {
                user.setVimeoAccessToken(userMap.get(keyCustom).toString());
            }
            if (keyCustom.equals(Constants.BDDV2_CUSTOM_USERS_COMPANY)) {
                user.setCompany(userMap.get(keyCustom).toString());
            }

            if (keyCustom.equals(Constants.BDDV2_CUSTOM_USERS_LICENSEEND)) {
                 user.setLicenceEnd(new Timestamp(Double.valueOf(userMap.get(keyCustom).toString()).longValue()));
            }
        }

        user.setRoles(hashMapRoles);
        return user;
    }
}