package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.vyfe.helper.TagSetsFilteredHelper;
import fr.vyfe.model.OwnerModel;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.TemplateModel;
import fr.vyfe.repository.BaseListValueEventListener;
import fr.vyfe.repository.SessionRepository;
import fr.vyfe.repository.TagSetRepository;


public class CreateSessionViewModel extends VyfeViewModel {

    private MutableLiveData<ArrayList<TagSetModel>> tagSets;
    private MutableLiveData<TagSetModel> selectedTagSet;
    private MutableLiveData<String> sessionName;
    private String userId;
    private String selectedTagSetId;
    private String androidId;
    private String diplayName;

    public CreateSessionViewModel(String userId, String diplayName, String companyId, String androidId) {
        tagSetRepository = new TagSetRepository(userId, companyId);
        sessionRepository = new SessionRepository(companyId);
        tagSets = new MutableLiveData<>();
        sessionName = new MutableLiveData<>();
        this.userId = userId;
        this.androidId = androidId;
        this.diplayName = diplayName;

    }

    public void init(String titleSession, String tagSetId) {
        this.setSessionName(incrementGridTitle(titleSession));
        this.selectedTagSetId = tagSetId;
    }

    public MutableLiveData<TagSetModel> getSelectedTagSet() {
        if (selectedTagSet == null)
            this.selectedTagSet = new MutableLiveData<>();
        return selectedTagSet;
    }

    public void setSelectedTagSet(TagSetModel selectedTagSet) {

        this.selectedTagSet.setValue(selectedTagSet);
        if (selectedTagSet != null) this.selectedTagSetId = selectedTagSet.getId();
    }

    public LiveData<ArrayList<TagSetModel>> getTagSets() {
        if (tagSets.getValue() == null) {
            loadTagSets();
        }
        return tagSets;
    }

    @Override
    protected void onCleared() {
        tagSetRepository.removeListeners();
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
                for (TagSetModel tagSet : result) {
                    //selected TagsSet
                    if (selectedTagSetId != null) {
                        if (selectedTagSetId.equals(tagSet.getId()))
                            selectedTagSet.setValue(tagSet);
                    }
                }
                tagSets.setValue(tagSetModels);
            }

            @Override
            public void onError(Exception e) {
                tagSets.setValue(null);
            }
        });

    }

    public String pushSession() throws Exception {
        SessionModel session = new SessionModel();
        session.setName(this.sessionName.getValue());
        OwnerModel owner = new OwnerModel(this.userId, this.diplayName);
        session.setOwner(owner);

        TagSetModel tagsSets = selectedTagSet.getValue();
        tagsSets.setId(null);
        tagsSets.setOwner(null);
        tagsSets.setAuthor(null);
        //TODO : delete boolean session
        tagsSets.setShared(Boolean.parseBoolean(null));
        session.setTagsSet(selectedTagSet.getValue());
        return sessionRepository.push(session, this.androidId);
    }

    public String getSessionName() {
        return this.sessionName != null ? this.sessionName.getValue() : "";
    }

    public void setSessionName(String name) {
        this.sessionName.setValue(name);
    }

    private String incrementGridTitle(String titleName) {
        if (titleName.matches("^.*_[0-9]+")) {
            String[] parts = titleName.split("_");
            String versionNum = parts[parts.length - 1];

            StringBuilder name = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                if (i > 0) name.append("_");
                name.append(parts[i]);
            }

            int number = Integer.parseInt(versionNum);
            return name.toString() + "_" + String.valueOf(number + 1);

        } else
            return titleName + "_2";
    }
}
