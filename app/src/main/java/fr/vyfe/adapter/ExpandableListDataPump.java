package fr.vyfe.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.TemplateModel;

public class ExpandableListDataPump {
    public static HashMap<TagSetModel,  ArrayList<TemplateModel>> getData(ArrayList<TagSetModel> tagSetModel) {
        HashMap<TagSetModel, ArrayList<TemplateModel>> expandableListDetail1 = new HashMap<>();
if(tagSetModel!= null){
        for(TagSetModel tagSetModel1: tagSetModel){

            ArrayList<TemplateModel> templates = new ArrayList<TemplateModel>();
            for(TemplateModel templateModel:tagSetModel1.getTemplates()){
                templates.add(templateModel);
            }
            expandableListDetail1.put(tagSetModel1, templates);
        }
        return expandableListDetail1;
    }
    return null;
    }

}
