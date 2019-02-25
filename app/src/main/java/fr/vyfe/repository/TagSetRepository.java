package fr.vyfe.repository;


import com.google.firebase.database.Query;

import java.util.List;

import fr.vyfe.Constants;
import fr.vyfe.mapper.TagSetMapper;
import fr.vyfe.mapper.TemplateMapper;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.TemplateModel;


public class TagSetRepository extends FirebaseDatabaseRepository<TagSetModel> {

    public TagSetRepository(String userId, String companyId) {
        super(new TagSetMapper(), companyId, userId);
    }

    @Override
    protected String getRootNode() {
        return getCompany() + "/" + Constants.BDDV2_USERS_TAGSSETS_KEY+ "/";
    }

    // This methods has to work with BDD V2 strange architecture
    // It will be refactored with next BDD version
    public void createTemplates(String key, List<TemplateModel> templates) {
        TemplateMapper mapper = new TemplateMapper();

        for (TemplateModel templateModel : templates) {
            databaseReference.child(key).child(Constants.BDDV2_USERS_TEMPLATES_KEY).push().setValue(mapper.unMap(templateModel));
        }
    }


}
