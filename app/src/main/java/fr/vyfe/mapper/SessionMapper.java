package fr.vyfe.mapper;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import fr.vyfe.entity.SessionEntity;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagModel;

public class SessionMapper extends FirebaseMapper<SessionEntity, SessionModel> {

    @Override
    public SessionModel map(@NonNull SessionEntity sessionEntity, String key) {
        SessionModel session = new SessionModel();
        session.setAuthor(sessionEntity.getAuthor());
        session.setDate(new Date(sessionEntity.getDate()));
        session.setIdAndroid(sessionEntity.getIdAndroid());
        session.setIdTagSet(sessionEntity.getTagSetId());
        session.setName(sessionEntity.getName());
        session.setDeviceVideoLink(sessionEntity.getPathApp());
        session.setServerVideoLink(sessionEntity.getVideoLink());
        session.setThumbnail(sessionEntity.getThumbnailUrl());
        session.setDescription(sessionEntity.getDescription());
        session.setId(key);
        ArrayList<TagModel> tagModels = new TagMapper().mapList(sessionEntity.getTags());
        session.setTags( tagModels);

        return session;
    }

    @Override
    public SessionEntity unMap(SessionModel sessionModel) {
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setAuthor(sessionModel.getAuthor());
        sessionEntity.setDate(sessionModel.getDate().getTime());
        sessionEntity.setDescription(sessionModel.getDescription());
        sessionEntity.setIdAndroid(sessionModel.getIdAndroid());
        sessionEntity.setTagSetId(sessionModel.getTagSetId());
        sessionEntity.setName(sessionModel.getName());
        sessionEntity.setPathApp(sessionModel.getDeviceVideoLink());
        sessionEntity.setTags((new TagMapper()).unMapList(sessionModel.getTags()));
        sessionEntity.setVideoLink(sessionModel.getServerVideoLink());
        return sessionEntity;
    }

    @Override
    public HashMap<String, SessionEntity> unMapList(List<SessionModel> models) {
        return null;
    }

}