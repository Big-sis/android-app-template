package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

import fr.vyfe.model.ColorModel;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.repository.TagSetRepository;


public class CreateGridViewModel extends VyfeViewModel {

    private MutableLiveData<String> tagSetName;
    private MutableLiveData<ArrayList<TagModel>> tags;

    CreateGridViewModel(String userId, String companyId) {
        tagSetRepository = new TagSetRepository(userId, companyId);
        tagSetName = new MutableLiveData<>();
        tags = new MutableLiveData<>();
    }

    public void init() {
        if (tags.getValue() == null) tags.setValue(new ArrayList<TagModel>());
    }

    public LiveData<String> getTagSetName(){
        return tagSetName;
    }

    public LiveData<ArrayList<TagModel>> getTags() {
        return tags;
    }

    @Override
    protected void onCleared() {
        tagSetRepository.removeListeners();
    }

    public void addTag(ColorModel color, String name) {
        TagModel tag = new TagModel();
        tag.setColor(color);
        tag.setName(name);
        tags.getValue().add(tag);
    }

    public void moveTag(int from, int to) {
        TagModel movingTag = tags.getValue().get(from);
        tags.getValue().remove(movingTag);
        tags.getValue().add(to, movingTag);
    }

    public void setTagSetName(String name){
        this.tagSetName.setValue(name);
    }

    public TagSetModel save() {
        TagSetModel tagSetModel = new TagSetModel();
        tagSetModel.setName(this.tagSetName.getValue());
        String tagSetKey = tagSetRepository.push(tagSetModel);
        tagSetRepository.createTags(tagSetKey, this.tags.getValue());
        tagSetModel.setTags(this.tags.getValue());
        return tagSetModel;
    }
}
