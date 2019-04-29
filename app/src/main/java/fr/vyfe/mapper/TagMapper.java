package fr.vyfe.mapper;

import fr.vyfe.entity.OwnerEntity;
import fr.vyfe.entity.TagEntity;
import fr.vyfe.helper.ColorHelper;
import fr.vyfe.model.OwnerModel;
import fr.vyfe.model.TagModel;

public class TagMapper extends FirebaseMapper<TagEntity, TagModel> {

    @Override
    public TagModel map(TagEntity tagEntity, String key) {
        TagModel tagModel = new TagModel();
        tagModel.setId(key);
        tagModel.setName(tagEntity.getName());
        tagModel.setColor(ColorHelper.getInstance().findColorById(tagEntity.getColor()));
        tagModel.setTemplateId(tagEntity.getTemplateId());
        tagModel.setStart(tagEntity.getStart());
        tagModel.setEnd(tagEntity.getEnd());

        //Author
        OwnerModel author = null;
        if(tagEntity.getAuthor()!=null){
            author = new OwnerModel(tagEntity.getAuthor().getUid(),tagEntity.getAuthor().getDisplayName());
            tagModel.setAuthor(author);
        } else {
            //deprecated
            if (tagEntity.getTaggerId() != null) {
                author =new OwnerModel(tagEntity.getTaggerId(),null);
                tagModel.setAuthor(author);
            }
        }
        return tagModel;
    }

    @Override
    public TagEntity unMap(TagModel tagModel) {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setName(tagModel.getName());
        if (tagModel.getColor() != null)
            tagEntity.setColor(tagModel.getColor().getId());
        if(tagModel.getTemplateId()!= null && tagEntity.getAuthor()== null){
            tagEntity.setTemplateId(tagModel.getTemplateId());
        }

        if(tagModel.getAuthor()!= null){
            OwnerEntity author = new OwnerEntity(tagModel.getAuthor().getUid(),tagModel.getAuthor().getDiplayName());
            tagEntity.setAuthor(author);
        }
        tagEntity.setStart(tagModel.getStart());
        tagEntity.setEnd(tagModel.getEnd());
        return tagEntity;
    }
}