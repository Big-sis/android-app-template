package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import fr.vyfe.model.ColorModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.TemplateModel;
import fr.vyfe.repository.TagSetRepository;


public class CreateGridViewModel extends VyfeViewModel {

    private MutableLiveData<String> tagSetName;
    private MutableLiveData<ArrayList<TemplateModel>> templates;

    CreateGridViewModel(String userId, String companyId) {
        tagSetRepository = new TagSetRepository(userId, companyId);
        tagSetName = new MutableLiveData<>();
        templates = new MutableLiveData<>();
    }

    public void init() {
        if (templates.getValue() == null) templates.setValue(new ArrayList<TemplateModel>());
    }

    public LiveData<String> getTagSetName(){
        return tagSetName;
    }

    public LiveData<ArrayList<TemplateModel>> getTemplates() {
        return templates;
    }

    @Override
    protected void onCleared() {
        tagSetRepository.removeListeners();
    }

    public void addTemplate(ColorModel color, String name) {
        TemplateModel template = new TemplateModel();
        template.setColor(color);
        template.setName(name);
        template.setPosition(templates.getValue().size());
        templates.getValue().add(template);
    }

    public void moveTag(int from, int to) {
        TemplateModel movingTemplate = templates.getValue().get(from);
        templates.getValue().remove(movingTemplate);
        templates.getValue().add(to, movingTemplate);
        templates.getValue().get(to).setPosition(to);
    }

    public void setTagSetName(String name){
        this.tagSetName.setValue(name);
    }

    public TagSetModel save() {
        TagSetModel tagSetModel = new TagSetModel();
        tagSetModel.setName(this.tagSetName.getValue());
        String tagSetKey = tagSetRepository.push(tagSetModel);
        tagSetRepository.createTemplates(tagSetKey, this.templates.getValue());
        tagSetModel.setTagTemplates(this.templates.getValue());
        return tagSetModel;
    }
}
