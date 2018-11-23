package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.repository.BaseListValueEventListener;
import fr.vyfe.repository.BaseSingleValueEventListener;
import fr.vyfe.repository.SessionRepository;
import fr.vyfe.repository.TagRepository;
import fr.vyfe.repository.TagSetRepository;

public class RecordVideoViewModel extends VyfeViewModel {

    public static final String STEP_RECODRING = "recording";
    public static final String STEP_STOP = "stop";
    public static final String STEP_ERROR = "error";
    public static final String STEP_SAVE = "save";
    public static final String STEP_CLOSE = "close";

    private MutableLiveData<SessionModel> session;
    private SessionRepository sessionRepository;
    private MutableLiveData<TagSetModel> tagSet;
    private TagSetRepository tagSetRepository;
    private TagRepository tagRepository;
    private MutableLiveData<String> stepRecord;
    private MutableLiveData<Long> videoTime;
    private MutableLiveData<List<TagModel>> tags;
    private String sessionId;

    public RecordVideoViewModel(String userId, String companyId, String sessionId) {
        sessionRepository = new SessionRepository(companyId);
        tagSetRepository = new TagSetRepository(userId, companyId);
        tagRepository = new TagRepository(companyId, userId, sessionId);
        stepRecord = new MutableLiveData<>();
        videoTime = new MutableLiveData<>();
        this.sessionId = sessionId;
    }

    public void init() {
        stepRecord.setValue("init");
    }

    public boolean isRecording() {
        return stepRecord.getValue().equals(STEP_RECODRING);
    }

    public MutableLiveData<String> getStep() {
        return stepRecord;
    }

    public void startRecord() {
        stepRecord.setValue(STEP_RECODRING);
    }

    public void stop() {
        stepRecord.setValue(STEP_STOP);
    }

    public void error() {
        stepRecord.setValue(STEP_ERROR);
    }

    public void save() {
        stepRecord.setValue(STEP_SAVE);
    }

    public void close() {
        stepRecord.setValue(STEP_CLOSE);
    }

    public LiveData<Long> getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(Long time) {
        this.videoTime.setValue(time);
    }

    public MutableLiveData<SessionModel> getSession() {
        if (session == null)
            loadSession(this.sessionId);
        return session;
    }

    public boolean addTag(int position) {
        if (tagSet.getValue() != null) {
            TagModel newTag = tagSet.getValue().getTags().get(position);
            tagRepository.push(newTag);
            return true;
        } else return false;
    }

    private void loadTagSet(String id) {
        tagSetRepository.addChildListener(id, true, new BaseSingleValueEventListener.CallbackInterface<TagSetModel>() {
            @Override
            public void onSuccess(TagSetModel result) {
                tagSet.postValue(result);
            }

            @Override
            public void onError(Exception e) {
                tagSet.setValue(null);
            }
        });
    }

    public MutableLiveData<TagSetModel> getTagSet() {
        if (tagSet == null) {
            tagSet = new MutableLiveData<>();
            // Load session here because loadTagSet function is chained with loadSession
            loadSession(this.sessionId);
        }
        return tagSet;
    }

    public MutableLiveData<List<TagModel>> getTags() {
        if (tags == null) {
            tags = new MutableLiveData<>();
            loadTags();
        }
        return tags;
    }

    private void loadTags() {
        tagRepository.addListListener(new BaseListValueEventListener.CallbackInterface<TagModel>() {
            @Override
            public void onSuccess(List<TagModel> result) {
                tags.postValue(result);
            }

            @Override
            public void onError(Exception e) {
                tags.setValue(null);
            }
        });
    }

    public String getSessionId() {
        return sessionId;
    }
}
