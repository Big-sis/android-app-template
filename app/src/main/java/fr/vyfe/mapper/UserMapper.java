package fr.vyfe.mapper;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.vyfe.entity.UserEntity;
import fr.vyfe.model.UserModel;

public class UserMapper extends FirebaseMapper<UserEntity, UserModel> {

    @Override
    public UserModel map(UserEntity userEntity, String key) {
        UserModel userModel = new UserModel();
        if (userEntity.getFirstName() != null) userModel.setFirstname(userEntity.getFirstName());
        if (userEntity.getLastName() != null) userModel.setLastName(userEntity.getLastName());
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
         }**/
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


        return user;
    }

    public UserModel map(Map<String, Object> customs) {
        UserModel user = new UserModel();
        HashMap<String, Boolean> hashMapRoles = new HashMap<>();
        for (String keyCustom : customs.keySet()) {
            if (keyCustom.equals("user_id")) {
                user.setId(customs.get(keyCustom).toString());
            }
            if (keyCustom.equals("teacher") || keyCustom.equals("observer") || keyCustom.equals("admin") || keyCustom.equals("student")) {
                hashMapRoles.put(keyCustom, new Boolean(customs.get(keyCustom).toString()));
            }

            if (keyCustom.equals("vimeoToken")) {
                user.setVimeoAccessToken(customs.get(keyCustom).toString());
            }
            if (keyCustom.equals("company")) {
                user.setCompany(customs.get(keyCustom).toString());
            }

            if (keyCustom.equals("licenseEnd")) {
                user.setLicenceEnd(new Timestamp(Long.parseLong(customs.get(keyCustom).toString())));
            }
        }

        user.setRolesC(hashMapRoles);
        return user;
    }
}