package fr.wildcodeschool.vyfe.repository;

import fr.wildcodeschool.vyfe.entity.SessionEntity;
import fr.wildcodeschool.vyfe.model.SessionModel;

public class SessionMapper extends FirebaseMapper<SessionEntity, SessionModel> {

    @Override
    public SessionModel map(SessionEntity sessionEntity) {
        SessionModel session = new SessionModel();
        session.setAuthor(sessionEntity.getAuthor());
        session.setName(sessionEntity.getName());
        session.setDate(sessionEntity.getDate());
        session.setDescription(sessionEntity.getDescription());
        session.setVideoLink(sessionEntity.getVideoLink());
        session.setIdTagSet(sessionEntity.getIdTagSet());
        session.setIdSession(sessionEntity.getIdSession());
        session.setIdAndroid(sessionEntity.getIdAndroid());
        session.setTags(sessionEntity.getTags());
        return session;
    }
}