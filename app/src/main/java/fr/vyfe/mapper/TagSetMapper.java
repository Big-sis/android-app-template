package fr.vyfe.mapper;


import android.support.annotation.NonNull;

import fr.vyfe.entity.OwnerEntity;
import fr.vyfe.entity.TagSetEntity;
import fr.vyfe.model.OwnerModel;
import fr.vyfe.model.TagSetModel;


public class TagSetMapper extends FirebaseMapper<TagSetEntity, TagSetModel> {
    @Override
    public TagSetModel map(TagSetEntity tagSetEntity, String key) {
        TagSetModel tagSet = new TagSetModel();
        if (tagSetEntity.getName() != null) tagSet.setName(tagSetEntity.getName());
        if (tagSetEntity.getOwner() != null) tagSet.setOwner(tagSetEntity.getOwner());

        //Author
        OwnerModel author = null;
        if(tagSetEntity.getAuthor()!=null){
            author = new OwnerModel(tagSetEntity.getAuthor().getUid(),tagSetEntity.getAuthor().getDisplayName());
            tagSet.setAuthor(author);
        } else {
            //deprecated
            if (tagSetEntity.getOwner() != null) {
                author =new OwnerModel(tagSetEntity.getOwner(),null);
                tagSet.setAuthor(author);
            }
        }

        tagSet.setShared(tagSetEntity.isShared());
        tagSet.setTagTemplates((new TemplateMapper()).mapList(tagSetEntity.getTemplates()));
        tagSet.setId(key);
        return tagSet;
    }

    @Override
    public TagSetEntity unMap(@NonNull TagSetModel tagSetModel) {
        TagSetEntity tagSetEntity = new TagSetEntity();
        if (tagSetModel != null) {
            if (tagSetModel.getName() != null) tagSetEntity.setName(tagSetModel.getName());
            if (tagSetModel.getOwner() != null) tagSetEntity.setOwner(tagSetModel.getOwner());

            if(tagSetModel.getAuthor()!= null){
                OwnerEntity author = new OwnerEntity(tagSetModel.getAuthor().getUid(),tagSetModel.getAuthor().getDiplayName());
                tagSetEntity.setAuthor(author);
            }

            tagSetEntity.setShared(tagSetModel.isShared());
            tagSetEntity.setTemplates((new TemplateMapper()).unMapList(tagSetModel.getTemplates()));
        }
        return tagSetEntity;
    }
}
