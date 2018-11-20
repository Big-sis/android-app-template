package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.repository.BaseListValueEventListener;
import fr.vyfe.repository.SessionRepository;
import fr.vyfe.repository.TagSetRepository;


public class CreateSessionViewModel extends ViewModel {

    private TagSetRepository tagSetRepository;
    private SessionRepository sessionRepository;
    private MutableLiveData<ArrayList<TagSetModel>> tagSets;
    private MutableLiveData<TagSetModel> selectedTagSet;
    private MutableLiveData<String> sessionName;
    private String userId;

    public CreateSessionViewModel(String userId, String companyId) {
        tagSetRepository = new TagSetRepository(userId, companyId);
        sessionRepository = new SessionRepository(companyId);
        tagSets = new MutableLiveData<>();
        sessionName = new MutableLiveData<>();
        this.userId = userId;
    }

    public MutableLiveData<TagSetModel> getSelectedTagSet() {
        if (selectedTagSet == null)
            this.selectedTagSet = new MutableLiveData<>();
        return selectedTagSet;
    }

    public void setSelectedTagSet(TagSetModel selectedTagSet) {
        this.selectedTagSet.setValue(selectedTagSet);
    }

    public LiveData<ArrayList<TagSetModel>> getTagSets() {
        if (tagSets.getValue() == null) {
            loadTagSets();
        }
        return tagSets;
    }

    public void setSessionName(String name) {
        this.sessionName.setValue(name);
    }

    @Override
    protected void onCleared() {
        tagSetRepository.removeListeners();
    }

    private void loadTagSets() {
        tagSetRepository.addListListener(new BaseListValueEventListener.CallbackInterface<TagSetModel>() {
            @Override
            public void onSuccess(List<TagSetModel> result) {
                tagSets.setValue((ArrayList) result);
            }

            @Override
            public void onError(Exception e) {
                tagSets.setValue(null);
            }
        });

    }

    public SessionModel getSession() {
        SessionModel session = new SessionModel();
        session.setName(this.sessionName.getValue());
        session.setAuthor(this.userId);
        if (this.selectedTagSet.getValue() != null) {
            session.setIdTagSet(this.selectedTagSet.getValue().getId());
            session.setTags(this.selectedTagSet.getValue().getTags());
        }
        return session;
    }
}
