package fr.vyfe.mapper;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

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
        if(sessionEntity.getDuration()!=-1)session.setDuration(sessionEntity.getDuration());
        session.setTags(tagModels);
        if (sessionEntity.getRecording() != null)
            session.setRecording(sessionEntity.getRecording());
        if (sessionEntity.getCooperative() != null)
            session.setCooperative(sessionEntity.getCooperative());

        ArrayList<String> observers = new ArrayList<>();
        if (sessionEntity.getObservers() != null) {
            for (String observer : Objects.requireNonNull(sessionEntity).getObservers().keySet()) {
                observers.add(observer);
            }
        }
        session.setObservers(observers);

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
        sessionEntity.setRecording(sessionModel.isRecording());
        sessionEntity.setCooperative(sessionModel.isCooperative());
       if(sessionModel.getDuration()!=-1)sessionEntity.setDuration(sessionModel.getDuration());
        return sessionEntity;
    }

}