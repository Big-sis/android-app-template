package fr.vyfe.mapper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.vyfe.entity.SessionEntity;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagModel;

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
    public SessionEntity unMap(SessionModel sessionModel) {
        return null;
    }

    @Override
    public HashMap<String, SessionEntity> unMapList(List<SessionModel> to) {
        return null;
    }

}