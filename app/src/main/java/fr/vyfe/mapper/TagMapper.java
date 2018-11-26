package fr.vyfe.mapper;

import fr.vyfe.entity.TagEntity;
import fr.vyfe.helper.ColorHelper;
import fr.vyfe.model.TagModel;

public class TagMapper extends FirebaseMapper<TagEntity, TagModel> {

    @Override
    public TagModel map(TagEntity tagEntity, String key) {
        TagModel tagModel = new TagModel();
        tagModel.setId(key);
        tagModel.setName(tagEntity.getName());
        tagModel.setColor(ColorHelper.getInstance().findColorById(tagEntity.getColor()));
        tagModel.setTemplateId(tagEntity.getTemplateId());
        tagModel.setTaggerId(tagEntity.getTaggerId());
        tagModel.setStart(tagEntity.getStart());
        tagModel.setEnd(tagEntity.getEnd());
        return tagModel;
    }

    @Override
    public TagEntity unMap(TagModel tagModel) {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setName(tagModel.getName());
        if (tagModel.getColor() != null)
            tagEntity.setColor(tagModel.getColor().getId());
        tagEntity.setTemplateId(tagModel.getTemplateId());
        tagEntity.setTaggerId(tagModel.getTaggerId());
        tagEntity.setStart(tagModel.getStart());
        tagEntity.setEnd(tagModel.getEnd());
        return tagEntity;
    }
}