package fr.wildcodeschool.vyfe.repository;

import fr.wildcodeschool.vyfe.entity.TimeEntity;
import fr.wildcodeschool.vyfe.model.TimeModel;

class TimeMapper extends FirebaseMapper<TimeEntity, TimeModel> {

    @Override
    public TimeModel map(TimeEntity timeEntity) {
        TimeModel time = new TimeModel();
        time.setEnd(timeEntity.getEnd());
        time.setStart(timeEntity.getStart());
        return time;
    }

}
