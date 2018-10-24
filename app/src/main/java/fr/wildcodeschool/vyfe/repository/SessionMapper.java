package fr.wildcodeschool.vyfe.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.wildcodeschool.vyfe.entity.SessionEntity;
import fr.wildcodeschool.vyfe.entity.TagEntity;
import fr.wildcodeschool.vyfe.entity.TimeEntity;
import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.model.TagModel;
import fr.wildcodeschool.vyfe.model.TimeModel;

public class SessionMapper extends FirebaseMapper<SessionEntity, SessionModel> {

    @Override
    public SessionModel map(SessionEntity sessionEntity) {
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
        ArrayList<TagModel> tagModels = new ArrayList<>();
        for (Map.Entry<String, TagEntity> tag : sessionEntity.getTags().entrySet())
        {

            for (Map.Entry<String, ArrayList<TimeEntity>> times : tag.getValue().getTimes().entrySet()) {
                TagModel tagModel = (new TagMapper()).map(tag.getValue());
                ArrayList<TimeModel> tagModelTimes = new ArrayList<>();
                for (TimeEntity time: times.getValue() ) {
                    TimeModel timeModel = (new TimeMapper()).map(time);
                    tagModelTimes.add(timeModel);
                }
                tagModel.setTimes(tagModelTimes);
                tagModel.setTaggerId(times.getKey());
                tagModel.setTagId(tag.getKey());
                tagModels.add(tagModel);
            }
        }
        session.setTags(tagModels);

        return session;
    }
}