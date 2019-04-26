package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import fr.vyfe.helper.TagSetsFilteredHelper;
import fr.vyfe.model.ColorModel;
import fr.vyfe.model.OwnerModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.TemplateModel;
import fr.vyfe.repository.BaseListValueEventListener;
import fr.vyfe.repository.TagSetRepository;


public class CreateGridViewModel extends VyfeViewModel {

    private MutableLiveData<String> tagSetName;
    private MutableLiveData<ArrayList<TemplateModel>> templates;
    private String userId;
    private String displayName;
    private MutableLiveData<TagSetModel> mTagsSetModel;

    private MutableLiveData<ArrayList<TagSetModel>> allTagSets;
    private MutableLiveData<TagSetModel> selectedTagSet;


    CreateGridViewModel(String userId, String displayName, String companyId) {
        tagSetRepository = new TagSetRepository(userId, companyId);
        tagSetName = new MutableLiveData<>();
        templates = new MutableLiveData<>();

        mTagsSetModel = new MutableLiveData<>();
        this.userId = userId;
        this.displayName = displayName;

        allTagSets = new MutableLiveData<>();
        selectedTagSet = new MutableLiveData<>();

    }

    public void init() {
        if (templates.getValue() == null) templates.setValue(new ArrayList<TemplateModel>());
    }

    public LiveData<ArrayList<TagSetModel>> getAllTagSets() {
        if (allTagSets.getValue() == null) {
            loadTagSets();
        }
        return allTagSets;
    }

    public MutableLiveData<TagSetModel> getSelectedTagSet() {
        if (selectedTagSet == null)
            this.selectedTagSet = new MutableLiveData<>();
        return selectedTagSet;
    }

    public void setSelectedTagSet(TagSetModel selectedTagSet) {

        this.selectedTagSet.setValue(selectedTagSet);

    }

    private void loadTagSets() {
        tagSetRepository.addListListener(new BaseListValueEventListener.CallbackInterface<TagSetModel>() {
            @Override
            public void onSuccess(List<TagSetModel> result) {
                for (TagSetModel tagSet : result) {
                    Collections.sort(tagSet.getTemplates(), new Comparator<TemplateModel>() {
                        @Override
                        public int compare(TemplateModel o1, TemplateModel o2) {
                            return o1.getPosition() - o2.getPosition();
                        }
                    });
                }

                ArrayList<TagSetModel> tagSetModels = TagSetsFilteredHelper.tagSetByAuthorAndShared(result, userId);

                allTagSets.setValue(tagSetModels);
            }

            @Override
            public void onError(Exception e) {
                allTagSets.setValue(null);
            }
        });

    }

    public void deleteTagSets(String idTagSet) {
        tagSetRepository.deleteTagSets(idTagSet);
    }

    public LiveData<String> getTagSetName() {
        return tagSetName;
    }

    public void setTagSetName(String name) {
        this.tagSetName.setValue(name);
    }

    public LiveData<ArrayList<TemplateModel>> getTemplates() {
        return templates;
    }

    public MutableLiveData<TagSetModel> getmTagsSetModel() {
        return mTagsSetModel;
    }

    public void setmTagsSetModel(MutableLiveData<TagSetModel> mTagsSetModel) {
        this.mTagsSetModel = mTagsSetModel;
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


    public void moveItem(int oldPos, int newPos) {
        if (oldPos < newPos) {
            for (int i = oldPos; i < newPos; i++) {
                Collections.swap(Objects.requireNonNull(getTemplates().getValue()), i, i + 1);
                getTemplates().getValue().get(i).setPosition(i);
                getTemplates().getValue().get(i + 1).setPosition(i + 1);
            }
        } else {
            for (int i = oldPos; i > newPos; i--) {
                Collections.swap(Objects.requireNonNull(getTemplates().getValue()), i, i - 1);
                getTemplates().getValue().get(i).setPosition(i);
                getTemplates().getValue().get(i - 1).setPosition(i - 1);
            }
        }
    }

    public void deleteItem(int position) {
        for (int i = position + 1; i < getTemplates().getValue().size(); i++) {
            getTemplates().getValue().get(i).setPosition(getTemplates().getValue().get(i).getPosition() - 1);
        }
        Objects.requireNonNull(getTemplates().getValue()).remove(position);
    }

    public TagSetModel save() {
        TagSetModel tagSetModel = new TagSetModel();
        tagSetModel.setName(this.tagSetName.getValue());
        tagSetModel.setAuthor(new OwnerModel(this.userId, displayName));
        String tagSetKey = tagSetRepository.push(tagSetModel);
        tagSetRepository.createTemplates(tagSetKey, this.templates.getValue());
        tagSetModel.setTagTemplates(this.templates.getValue());
        return tagSetModel;
    }
}
