package fr.wildcodeschool.vyfe.repository;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import fr.wildcodeschool.vyfe.entity.SessionEntity;
import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.model.TagModel;

public class SessionMapper extends FirebaseMapper<SessionEntity, SessionModel> {

    @Override
    public SessionModel map(SessionEntity sessionEntity, String key) {
        SessionModel session = new SessionModel();
        session.setAuthor(sessionEntity.getAuthor());
        session.setDate(sessionEntity.getDate());
        session.setIdAndroid(sessionEntity.getIdAndroid());
        session.setIdTagSet(sessionEntity.getIdTagSet());
        session.setName(sessionEntity.getName());
        session.setDeviceVideoLink(sessionEntity.getPathApp());
        session.setVideoLink(sessionEntity.getVideoLink());
        session.setThumbnail(sessionEntity.getThumbnailUrl());
        session.setDescription(sessionEntity.getDescription());
        session.setIdSession(key);
        ArrayList<TagModel> tagModels = new TagMapper().mapList(sessionEntity.getTags());

        session.setTags( tagModels);

        return session;
    }

    @Override
    public List<SessionModel> mapList(HashMap<String,SessionEntity> from) {
        return null;
    }
}