package fr.wildcodeschool.vyfe.repository;

import java.util.ArrayList;
import java.util.Map;

import fr.wildcodeschool.vyfe.entity.TagEntity;
import fr.wildcodeschool.vyfe.entity.TimeEntity;
import fr.wildcodeschool.vyfe.model.TagModel;
import fr.wildcodeschool.vyfe.model.TimeModel;

public class TagMapper extends FirebaseMapper<TagEntity, TagModel> {

    @Override
    public TagModel map(TagEntity tagEntity) {
        TagModel tag = new TagModel();
        tag.setColor(tagEntity.getColor());
        tag.setTagName(tagEntity.getName());

        return tag;
    }
}