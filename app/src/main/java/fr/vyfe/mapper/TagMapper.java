package fr.vyfe.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.vyfe.entity.TagEntity;
import fr.vyfe.entity.TimeEntity;
import fr.vyfe.helper.ColorHelper;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TimeModel;

public class TagMapper extends FirebaseMapper<TagEntity, TagModel> {

    @Override
    public TagModel map(TagEntity tagEntity, String key) {
        TagModel tag = new TagModel();
        tag.setColor(ColorHelper.getInstance().findColorById(tagEntity.getColor()));
        tag.setName(tagEntity.getName());
        tag.setId(key);

        if (tagEntity.getTimes() != null) {
            for (Map.Entry<String, ArrayList<TimeEntity>> mapTime : tagEntity.getTimes().entrySet()) {
                tag.setTaggerId(mapTime.getKey());

                ArrayList<TimeModel> times = new ArrayList<>();
                for (TimeEntity time : mapTime.getValue()) {
                    TimeModel timeStartEnd = new TimeModel();
                    timeStartEnd.setStart(time.getStart());
                    timeStartEnd.setEnd(time.getEnd());
                    times.add(timeStartEnd);
                }
                tag.setTimes(times);
            }
        }

        return tag;
    }

    @Override
    public ArrayList<TagModel> mapList(HashMap<String, TagEntity> from) {
        ArrayList<TagModel> tagModelList = new ArrayList<>();
        if (from != null) {
            for (Map.Entry<String, TagEntity> tag : from.entrySet()) {
                tagModelList.add(map(tag.getValue(), tag.getKey()));
            }
        }
        return tagModelList;
    }

    @Override
    public TagEntity unMap(TagModel tagModel) {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setName(tagModel.getName());
        tagEntity.setColor(tagModel.getColor().getId());
        tagEntity.setLeftOffset(tagModel.getLeftOffset());
        tagEntity.setRigthOffset(tagModel.getRigthOffset());
        return tagEntity;
    }

    @Override
    public HashMap<String, TagEntity> unMapList(List<TagModel> to) {
        return null;
    }
}