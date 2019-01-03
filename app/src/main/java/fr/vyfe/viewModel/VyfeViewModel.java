package fr.vyfe.viewModel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.Collections;
import java.util.Comparator;

import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.TemplateModel;
import fr.vyfe.repository.BaseSingleValueEventListener;
import fr.vyfe.repository.SessionRepository;
import fr.vyfe.repository.TagSetRepository;

public abstract class VyfeViewModel extends ViewModel {

    protected SessionRepository sessionRepository;
    protected TagSetRepository tagSetRepository;
    protected MutableLiveData<SessionModel> session;
    protected MutableLiveData<TagSetModel> tagSet;
    protected String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    protected void loadSession(String id) {
        if (session == null)
            session = new MutableLiveData<>();


        sessionRepository.addChildListener(id, true, new BaseSingleValueEventListener.CallbackInterface<SessionModel>() {
            @Override
            public void onSuccess(SessionModel result) {

                session.postValue(result);
                if (tagSetRepository != null)
                    loadTagSet(result.getTagSetId());

            }

            @Override
            public void onError(Exception e) {
                session.setValue(null);
            }
        });
    }

    private void loadTagSet(String id) {
        if (tagSet == null)
            tagSet = new MutableLiveData<>();
        tagSetRepository.addChildListener(id, true, new BaseSingleValueEventListener.CallbackInterface<TagSetModel>() {
            @Override
            public void onSuccess(TagSetModel result) {

                Collections.sort(result.getTemplates(), new Comparator<TemplateModel>() {
                    @Override
                    public int compare(TemplateModel o1, TemplateModel o2) {
                        return o1.getPosition() - o2.getPosition();
                    }
                });
                tagSet.postValue(result);
            }

            @Override
            public void onError(Exception e) {
                tagSet.setValue(null);
            }
        });
    }

    public MutableLiveData<SessionModel> getSession() {
        if (session == null)
            loadSession(this.sessionId);
        return session;
    }

    public MutableLiveData<TagSetModel> getTagSet() {
        if (tagSet == null) {
            tagSet = new MutableLiveData<>();
        }
        loadSession(this.sessionId);
        return tagSet;
    }

    @Override
    protected void onCleared() {
        if (sessionRepository != null) sessionRepository.removeListeners();
        if (tagSetRepository != null) tagSetRepository.removeListeners();
    }
}
