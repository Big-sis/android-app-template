package fr.vyfe.mapper;

import fr.vyfe.entity.TemplateEntity;
import fr.vyfe.helper.ColorHelper;
import fr.vyfe.model.TemplateModel;

public class TemplateMapper extends FirebaseMapper<TemplateEntity, TemplateModel> {

    @Override
    public TemplateModel map(TemplateEntity templateEntity, String key) {
        TemplateModel templateModel = new TemplateModel();
        templateModel.setColor(ColorHelper.getInstance().findColorById(templateEntity.getColor()));
        templateModel.setName(templateEntity.getName());
        templateModel.setLeftOffset(templateEntity.getLeftOffset());
        templateModel.setRigthOffset(templateEntity.getRigthOffset());
        templateModel.setId(key);
        return templateModel;
    }

    @Override
    public TemplateEntity unMap(TemplateModel templateModel) {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setName(templateModel.getName());
        templateEntity.setColor(templateModel.getColor().getId());
        templateEntity.setLeftOffset(templateModel.getLeftOffset());
        templateEntity.setRigthOffset(templateModel.getRigthOffset());
        return templateEntity;
    }
}