package fr.vyfe.repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.vyfe.mapper.TagSetMapper;
import fr.vyfe.mapper.TemplateMapper;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.TemplateModel;


public class TagSetRepository extends FirebaseDatabaseRepository<TagSetModel>{

    public TagSetRepository(String userId, String companyId) {
        super(new TagSetMapper(), companyId, userId);
    }

    @Override
    protected String getRootNode() {
        return getCompany() + "/Users/" + this.getUser() + "/TagSets/";
    }

    // This methods has to work with BDD V2 strange architecture
    // It will be refactored with next BDD version
    public void createTemplates(String key, List<TemplateModel> templates){
        TemplateMapper mapper = new TemplateMapper();

        Collections.sort(templates, new Comparator<TemplateModel>() {
            @Override
            public int compare(TemplateModel o1, TemplateModel o2) {
                return o1.getPosition()-o2.getPosition();
            }
        });
        for (TemplateModel templateModel: templates) {
            databaseReference.child(key).child("Templates").push().setValue(mapper.unMap(templateModel));
        }
    }
}
