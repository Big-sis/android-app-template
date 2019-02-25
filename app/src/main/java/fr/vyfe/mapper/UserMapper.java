package fr.vyfe.mapper;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

   /** public UserModel map(HashMap<String, Object> userMap) {
        UserModel user = new UserModel();
        user.setId((String) userMap.get("id"));
        user.setCompany((String) userMap.get("company"));
        if (userMap.containsKey("profile")) {
            user.setFirstname(((HashMap<String, String>) userMap.get("profile")).get("firstName"));
            user.setLastName(((HashMap<String, String>) userMap.get("profile")).get("lastName"));
            if (((HashMap<String, String>) userMap.get("profile")).get("promo") != null)
                user.setPromo(((HashMap<String, String>) userMap.get("profile")).get("promo"));
        }/**
         try {
         if (userMap.get("license") != null)
         user.setLicenceEnd((new SimpleDateFormat("dd-MM-yy")).parse((String) userMap.get("license")));
         } catch (ParseException e) {
         e.printStackTrace();
         }
        ArrayList<String> roles = new ArrayList<>();
        if (((HashMap<String, Boolean>) userMap.get("roles")).get("admin") != null)
            if (((HashMap<String, Boolean>) userMap.get("roles")).get("admin"))
                roles.add("admin");

        if (((HashMap<String, Boolean>) userMap.get("roles")).get("teacher") != null) {
            if (((HashMap<String, Boolean>) userMap.get("roles")).get("teacher"))
                roles.add("teacher");
        }

        if (((HashMap<String, Boolean>) userMap.get("roles")).get("viewer") != null) {
            if (((HashMap<String, Boolean>) userMap.get("roles")).get("viewer"))
                roles.add("reviewer");
            user.setRoles(roles.toArray(new String[roles.size()]));
        }


        return null;
    }**/

    public UserModel map(HashMap<String, Object> customs) {
        UserModel user = new UserModel();
        HashMap<String, Boolean> hashMapRoles = new HashMap<>();
        for (String keyCustom : customs.keySet()) {
            if (keyCustom.equals("user_id")) {
                user.setId(customs.get(keyCustom).toString());
            }
            if (keyCustom.equals(Constants.BDDV2_CUSTOM_USERS_ROLE_TEACHER) || keyCustom.equals(Constants.BDDV2_CUSTOM_USERS_ROLE_OBSERVER) || keyCustom.equals(Constants.BDDV2_CUSTOM_USERS_ROLE_ADMIN) || keyCustom.equals(Constants.BDDV2_CUSTOM_USERS_ROLE_STUDENT)) {
                hashMapRoles.put(keyCustom, new Boolean(customs.get(keyCustom).toString()));
            }

            if (keyCustom.equals(Constants.BDDV2_CUSTOM_USERS_VIMEOACCESSTOKEN)) {
                user.setVimeoAccessToken(customs.get(keyCustom).toString());
            }
            if (keyCustom.equals(Constants.BDDV2_CUSTOM_USERS_COMPANY)) {
                user.setCompany(customs.get(keyCustom).toString());
            }

            if (keyCustom.equals(Constants.BDDV2_CUSTOM_USERS_LICENSEEND)) {
                //TODO : recup Custom
                user.setLicenceEnd(new Timestamp(new Double(1.5989112E12).longValue()));
                // user.setLicenceEnd(new Timestamp(Long.parseLong(customs.get(keyCustom).toString())));
            }
        }

        user.setRolesC(hashMapRoles);
        return user;
    }
}