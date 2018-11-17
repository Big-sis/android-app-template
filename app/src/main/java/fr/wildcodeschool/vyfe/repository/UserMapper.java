package fr.wildcodeschool.vyfe.repository;

import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fr.wildcodeschool.vyfe.entity.TagEntity;
import fr.wildcodeschool.vyfe.entity.TimeEntity;
import fr.wildcodeschool.vyfe.entity.UserEntity;
import fr.wildcodeschool.vyfe.model.TagModel;
import fr.wildcodeschool.vyfe.model.TimeModel;
import fr.wildcodeschool.vyfe.model.UserModel;

public class UserMapper extends FirebaseMapper<UserEntity, UserModel> {

    @Override
    public UserModel map(UserEntity userEntity, String key) {
        return map(userEntity, new UserModel(key));
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
        user.setEmail(((HashMap<String, String>)userMap.get("Profile")).get("email"));
        user.setFirstname(((HashMap<String, String>)userMap.get("Profile")).get("firstName"));
        user.setLastName(((HashMap<String, String>)userMap.get("Profile")).get("lastName"));
        user.setPromo(((HashMap<String, String>)userMap.get("Profile")).get("promo"));
        try {
            user.setLicenceEnd((new SimpleDateFormat("dd-MM-yy")).parse((String) userMap.get("license")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ArrayList<String> roles = new ArrayList<>();
        if (((HashMap<String, Boolean>)userMap.get("Roles")).get("admin"))
            roles.add("admin");
        if (((HashMap<String, Boolean>)userMap.get("Roles")).get("teacher"))
            roles.add("teacher");
        if (((HashMap<String, Boolean>)userMap.get("Roles")).get("viewer"))
            roles.add("reviewer");
        user.setRoles(roles.toArray(new String[roles.size()]));

        return user;
    }
}