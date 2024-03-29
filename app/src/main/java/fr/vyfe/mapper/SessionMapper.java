package fr.vyfe.mapper;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;

import fr.vyfe.entity.OwnerEntity;
import fr.vyfe.entity.SessionEntity;
import fr.vyfe.model.OwnerModel;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.TemplateModel;

public class SessionMapper extends FirebaseMapper<SessionEntity, SessionModel> {

    @Override
    public SessionModel map(@NonNull SessionEntity sessionEntity, String key) {
        SessionModel session = new SessionModel();
        if (sessionEntity != null) {

            if (sessionEntity.getTimestamp() != 0)
                session.setDate(new Date(sessionEntity.getTimestamp()));
            if (sessionEntity.getIdAndroid() != null)
                session.setIdAndroid(sessionEntity.getIdAndroid());
            if (sessionEntity.getName() != null) session.setName(sessionEntity.getName());
            if (sessionEntity.getPathApp() != null)
                session.setDeviceVideoLink(sessionEntity.getPathApp());
            if (sessionEntity.getVideoLink() != null)
                session.setServerVideoLink(sessionEntity.getVideoLink());
            if (sessionEntity.getThumbnailUrl() != null)
                session.setThumbnail(sessionEntity.getThumbnailUrl());
            if (sessionEntity.getDescription() != null)
                session.setDescription(sessionEntity.getDescription());
            session.setId(key);

            //Owner
            OwnerModel owner = null;
            if (sessionEntity.getOwner() != null) {
                owner = new OwnerModel(sessionEntity.getOwner().getUid(), sessionEntity.getOwner().getDisplayName());
                session.setOwner(owner);
            } else {
                //deprecated
                if (sessionEntity.getAuthor() != null) {
                    owner = new OwnerModel(sessionEntity.getAuthor(), null);
                    session.setOwner(owner);
                }
            }

            //TagsSet
            TagSetModel tagSet = new TagSetModel();
            if (sessionEntity.getTagSet() != null && sessionEntity.getTagSet().getTemplates() != null) {
                ArrayList<TemplateModel> templates = new TemplateMapper().mapList(sessionEntity.getTagSet().getTemplates());
                tagSet.setTagTemplates(templates);
                tagSet.setName(sessionEntity.getTagSet().getName());
                session.setTagsSet(tagSet);
            }

            //Tags
            ArrayList<TagModel> tagModels = new TagMapper().mapList(sessionEntity.getTags());
            session.setTags(tagModels);
            if (sessionEntity.getDuration() != -1) session.setDuration(sessionEntity.getDuration());
            if (sessionEntity.getRecording() != null)
                session.setRecording(sessionEntity.getRecording());
            if (sessionEntity.getCooperative() != null)
                session.setCooperative(sessionEntity.getCooperative());

            //Observers
            if (sessionEntity.getObservers() != null)
                session.setObservers(new ObserverMapper().mapList(sessionEntity.getObservers()));
        }
        return session;
    }

    @Override
    public SessionEntity unMap(SessionModel sessionModel) {
        SessionEntity sessionEntity = new SessionEntity();

        if (sessionModel.getOwner() != null) {
            OwnerEntity owner = new OwnerEntity(sessionModel.getOwner().getUid(), sessionModel.getOwner().getDiplayName());
            sessionEntity.setOwner(owner);
        }

        sessionEntity.setTimestamp(sessionModel.getDate().getTime());
        sessionEntity.setShared(true);
        if (sessionModel.getDescription() == null) {
            sessionEntity.setDescription("");
        } else sessionEntity.setDescription(sessionModel.getDescription());
        sessionEntity.setIdAndroid(sessionModel.getIdAndroid());
        sessionEntity.setName(sessionModel.getName());
        sessionEntity.setPathApp(sessionModel.getDeviceVideoLink());
        sessionEntity.setTags((new TagMapper()).unMapList(sessionModel.getTags()));
        sessionEntity.setTagSet((new TagSetMapper()).unMap(sessionModel.getTagsSet()));
        sessionEntity.setVideoLink(sessionModel.getServerVideoLink());
        sessionEntity.setRecording(sessionModel.isRecording());
        sessionEntity.setCooperative(sessionModel.isCooperative());
        if (sessionModel.getDuration() != -1) sessionEntity.setDuration(sessionModel.getDuration());

        if (sessionModel.getObservers() != null)
            sessionEntity.setObservers(new ObserverMapper().unMapList(sessionModel.getObservers()));
        else sessionEntity.setObservers(null);


        return sessionEntity;
    }
}