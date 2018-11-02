package fr.wildcodeschool.vyfe.repository;

import java.util.ArrayList;
import java.util.HashMap;

import fr.wildcodeschool.vyfe.entity.TimeEntity;
import fr.wildcodeschool.vyfe.model.TimeModel;

class TimeMapper extends FirebaseMapper<TimeEntity, TimeModel> {



    @Override
    public TimeModel map(TimeEntity timeEntity, String key) {
        TimeModel time = new TimeModel();
        time.setEnd(timeEntity.getEnd());
        time.setStart(timeEntity.getStart());
        return time;
    }

    @Override
    public ArrayList<TimeModel> mapList(HashMap<String, TimeEntity> from) {
        return null;
    }
}
