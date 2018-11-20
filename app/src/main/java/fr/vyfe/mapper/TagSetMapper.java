package fr.vyfe.mapper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fr.vyfe.entity.TagEntity;
import fr.vyfe.entity.TagSetEntity;
import fr.vyfe.helper.ColorHelper;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TagSetModel;


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
            tagModel.setName(tags.getValue().getName());
            tagModel.setColor(ColorHelper.getInstance().findColorById(tags.getValue().getColor()));

            tagslist.add(tagModel);

        }
        tagSet.setTags(tagslist);
        tagSet.setId(key);

        return tagSet;
    }

    @Override
    public TagSetEntity unMap(TagSetModel tagSetModel) {
        TagSetEntity tagSetEntity = new TagSetEntity();
        tagSetEntity.setName(tagSetModel.getName());
        return tagSetEntity;
    }

    @Override
    public HashMap<String, TagSetEntity> unMapList(List<TagSetModel> to) {
        return null;
    }

}
