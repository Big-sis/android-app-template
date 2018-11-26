package fr.vyfe.mapper;


import fr.vyfe.entity.TagSetEntity;
import fr.vyfe.model.TagSetModel;


public class TagSetMapper extends FirebaseMapper<TagSetEntity, TagSetModel> {
    @Override
    public TagSetModel map(TagSetEntity tagSetEntity, String key) {
        TagSetModel tagSet = new TagSetModel();
        tagSet.setName(tagSetEntity.getName());
        tagSet.setTagTemplates((new TemplateMapper()).mapList(tagSetEntity.getTags()));
        tagSet.setId(key);
        return tagSet;
    }

    @Override
    public TagSetEntity unMap(TagSetModel tagSetModel) {
        TagSetEntity tagSetEntity = new TagSetEntity();
        tagSetEntity.setName(tagSetModel.getName());
        tagSetEntity.setTags((new TemplateMapper()).unMapList(tagSetModel.getTemplates()));
        return tagSetEntity;
    }
}
