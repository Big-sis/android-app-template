package fr.wildcodeschool.vyfe.repository;

import android.content.Context;

import java.util.HashMap;

import fr.wildcodeschool.vyfe.entity.SessionEntity;
import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.model.TagModel;


public class SessionpostMapper {


    public SessionEntity SessionpostMapper(SessionModel sessionModel, Context context) {

        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setAuthor(sessionModel.getAuthor());
        sessionEntity.setDate(sessionModel.getDate());
        sessionEntity.setIdAndroid(sessionModel.getIdAndroid());
        sessionEntity.setName(sessionModel.getName());
        sessionEntity.setPathApp(sessionModel.getVideoLink());
        sessionEntity.setDescription(sessionModel.getDescription());
        sessionEntity.setIdTagSet(sessionModel.getIdTagSet());


        //sessionEntity.setTags();


        HashMap<String, TagModel> tagsList = new HashMap<>();
        for (TagModel tags : sessionModel.getTags())
            for (int i = 0; i < sessionModel.getTags().size(); i++) {
                String nameColor = sessionModel.getTags().get(i).getColor();
                String nameTag = sessionModel.getTags().get(i).getTagName();
              //  TagNameColorName.put(nameTag, nameColor);

            }

        return sessionEntity;

    }


}
