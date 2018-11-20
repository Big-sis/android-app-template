package fr.vyfe.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.vyfe.entity.TimeEntity;
import fr.vyfe.model.TimeModel;

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

    @Override
    public TimeEntity unMap(TimeModel timeModel) {
        return null;
    }

    @Override
    public HashMap<String, TimeEntity> unMapList(List<TimeModel> to) {
        return null;
    }
}
