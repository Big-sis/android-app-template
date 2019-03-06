package fr.vyfe.repository;


import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import fr.vyfe.Constants;
import fr.vyfe.entity.TagSetEntity;
import fr.vyfe.entity.TemplateEntity;
import fr.vyfe.mapper.TagSetMapper;
import fr.vyfe.mapper.TemplateMapper;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.TemplateModel;


public class TagSetRepository extends FirebaseDatabaseRepository<TagSetModel> {

    public TagSetRepository(String userId, String companyId) {
        super(new TagSetMapper(), companyId, userId);
    }

    @Override
    protected String getRootNode() {
        return getCompany() + "/" + Constants.BDDV2_USERS_TAGSETS_KEY+ "/";
    }

    // This methods has to work with BDD V2 strange architecture
    // It will be refactored with next BDD version
    public void createTemplates(String key, List<TemplateModel> templates) {
        TemplateMapper mapper = new TemplateMapper();

        for (TemplateModel templateModel : templates) {
            databaseReference.child(key).child(Constants.BDDV2_USERS_TEMPLATES_KEY).push().setValue(mapper.unMap(templateModel));
        }
    }

    public void deleteTagSets (String idTagSet){
        databaseReference.child(idTagSet).removeValue();
    }


    public Task<Void> update(TemplateModel model, SessionModel sessionModel) {
        model.setId(null);
        return FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL).getReference(getCompany()+"/sessions").child(sessionModel.getId()).child("tagSet").child("templates").child(model.getId()).updateChildren(((TemplateEntity) mapper.unMap(model)).toHashmap());
    }

}
