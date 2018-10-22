package fr.wildcodeschool.vyfe.repository;


import fr.wildcodeschool.vyfe.entity.SessionEntityBDD2;
import fr.wildcodeschool.vyfe.model.SessionModelBDD2;


public class SessionMapperBDD2 extends FirebaseMapper<SessionEntityBDD2, SessionModelBDD2> {

    @Override
    public SessionModelBDD2 map(SessionEntityBDD2 sessionEntity) {
        SessionModelBDD2 session = new SessionModelBDD2();
        session.setAuthor(sessionEntity.getAuthor());
        session.setDate(sessionEntity.getDate());
        session.setIdAndroid(sessionEntity.getIdAndroid());
        session.setIdTagSet(sessionEntity.getIdTagSet());
        session.setName(sessionEntity.getName());
        session.setPathApp(sessionEntity.getPathApp());
        session.setVideoLink(sessionEntity.getVideoLink());
        session.setThumbnailUrl(sessionEntity.getThumbnailUrl());
        session.setDescription(sessionEntity.getDescription());
        session.setTagsSessions(sessionEntity.getTagsSessions());

        return session;
    }


}