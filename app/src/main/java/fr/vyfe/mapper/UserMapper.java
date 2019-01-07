package fr.vyfe.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.vyfe.entity.UserEntity;
import fr.vyfe.model.UserModel;

public class UserMapper extends FirebaseMapper<UserEntity, UserModel> {

    @Override
    public UserModel map(UserEntity userEntity, String key) {
        return map(userEntity, new UserModel(key));
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
     * @param userMap
     * @return
     */
    public UserModel map(HashMap<String,Object> userMap) {
        UserModel user = new UserModel();
        user.setId((String) userMap.get("id"));
        user.setCompany((String) userMap.get("company"));
        if (userMap.containsKey("profile")) {
            user.setEmail(((HashMap<String, String>) userMap.get("profile")).get("email"));
            user.setFirstname(((HashMap<String, String>) userMap.get("profile")).get("firstName"));
            user.setLastName(((HashMap<String, String>) userMap.get("profile")).get("lastName"));
            user.setPromo(((HashMap<String, String>) userMap.get("profile")).get("promo"));
        }
        try {
            user.setLicenceEnd((new SimpleDateFormat("dd-MM-yy")).parse((String) userMap.get("license")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ArrayList<String> roles = new ArrayList<>();
        if (((HashMap<String, Boolean>)userMap.get("roles")).get("admin"))
            roles.add("admin");
        if (((HashMap<String, Boolean>)userMap.get("roles")).get("teacher"))
            roles.add("teacher");
        if (((HashMap<String, Boolean>)userMap.get("roles")).get("viewer"))
            roles.add("reviewer");
        user.setRoles(roles.toArray(new String[roles.size()]));

        return user;
    }
}