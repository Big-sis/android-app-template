package fr.wildcodeschool.vyfe.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.wildcodeschool.vyfe.entity.TagEntity;
import fr.wildcodeschool.vyfe.entity.TimeEntity;
import fr.wildcodeschool.vyfe.model.TagModel;
import fr.wildcodeschool.vyfe.model.TimeModel;

public class TagMapper extends FirebaseMapper<TagEntity, TagModel> {

    @Override
    public TagModel map(TagEntity tagEntity, String key) {
        TagModel tag = new TagModel();
        tag.setColor(tagEntity.getColor());
        tag.setTagName(tagEntity.getName());
        tag.setTagId(key);

        for(Map.Entry<String, ArrayList<TimeEntity>> mapTime : tagEntity.getTimes().entrySet()){
            tag.setTaggerId(mapTime.getKey());

            ArrayList<TimeModel> times = new ArrayList<>();
            for (TimeEntity time : mapTime.getValue()){
                TimeModel timeStartEnd = new TimeModel();
                timeStartEnd.setStart(time.getStart());
                timeStartEnd.setEnd(time.getEnd());
                times.add(timeStartEnd);
            }
            tag.setTimes(times);
        }

        return tag;
    }

    @Override
    public ArrayList<TagModel> mapList(HashMap<String,TagEntity> from) {
        ArrayList<TagModel> tagModelList = new ArrayList<>();
        for (Map.Entry<String,TagEntity>  tag : from.entrySet()){
            tagModelList.add(map(tag.getValue(),tag.getKey()));
        }

        return tagModelList;
    }
}