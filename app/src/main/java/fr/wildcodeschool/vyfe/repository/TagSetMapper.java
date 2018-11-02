package fr.wildcodeschool.vyfe.repository;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.wildcodeschool.vyfe.entity.TagEntity;
import fr.wildcodeschool.vyfe.entity.TagSetEntity;
import fr.wildcodeschool.vyfe.entity.TimeEntity;
import fr.wildcodeschool.vyfe.model.TagModel;
import fr.wildcodeschool.vyfe.model.TagSetModel;
import fr.wildcodeschool.vyfe.model.TimeModel;


public class TagSetMapper extends FirebaseMapper<TagSetEntity, TagSetModel> {
    @Override
    public TagSetModel map(TagSetEntity tagSetEntity, String key) {

        TagSetModel tagSet = new TagSetModel();
        tagSet.setName(tagSetEntity.getName());

        ArrayList<TagModel> tagslist = new ArrayList<>();

        for (Map.Entry<String, TagEntity> tags : tagSetEntity.getTags().entrySet()) {
            TagModel tagModel = new TagModel();
            tagModel.setTagId(tags.getKey());

            tagModel.setLeftOffset(tags.getValue().getLeftOffset());
            tagModel.setRigthOffset(tags.getValue().getRigthOffset());
            tagModel.setTagName(tags.getValue().getName());
            tagModel.setColor(tags.getValue().getColor());

            tagslist.add(tagModel);


        }
        tagSet.setTags(tagslist);
        tagSet.setId(key);

        return tagSet;
    }

    @Override
    public List<TagSetModel> mapList(HashMap<String, TagSetEntity> from) {
        return null;
    }


}
