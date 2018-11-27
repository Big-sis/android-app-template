package fr.vyfe.mapper;

import fr.vyfe.entity.TimeEntity;
import fr.vyfe.model.TimeModel;

class TimeMapper {

    public TimeModel map(TimeEntity timeEntity, String key) {
        TimeModel time = new TimeModel();
        time.setEnd(timeEntity.getEnd());
        time.setStart(timeEntity.getStart());
        return time;
    }
}
