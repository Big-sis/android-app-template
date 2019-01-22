package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import fr.vyfe.Constants;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TemplateModel;
import fr.vyfe.repository.BaseListValueEventListener;
import fr.vyfe.repository.SessionRepository;
import fr.vyfe.repository.TagRepository;
import fr.vyfe.repository.TagSetRepository;

public class RecordVideoViewModel extends VyfeViewModel {

    public static final String STEP_RECODRING = "recording";
    public static final String STEP_STOP = "stop";
    public static final String STEP_ERROR = "error";
    public static final String STEP_SAVE = "save";
    public static final String STEP_CLOSE = "close";
    public static final String STEP_DELETE = "delete";

    private TagRepository tagRepository;
    private MutableLiveData<String> stepRecord;
    private MutableLiveData<Long> videoTime;
    private MutableLiveData<List<TagModel>> tags;
    private String userId;
    private MutableLiveData<Boolean> areTagsActive;
    private MutableLiveData<Boolean> isLiveRecording;

    public RecordVideoViewModel(String userId, String companyId, String sessionId) {
        sessionRepository = new SessionRepository(companyId);
        tagSetRepository = new TagSetRepository(userId, companyId);
        tagRepository = new TagRepository(companyId, userId, sessionId);
        stepRecord = new MutableLiveData<>();
        videoTime = new MutableLiveData<>();
        this.sessionId = sessionId;
        this.userId = userId;
        areTagsActive = new MutableLiveData<>();
        isLiveRecording = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getAreTagsActive() {
        return areTagsActive;
    }

    public void setAreTagsActive(MutableLiveData<Boolean> areTagsActive) {
        this.areTagsActive = areTagsActive;
    }

    public void init() {
        stepRecord.setValue("init");
    }

    public boolean isRecording() {
        return stepRecord.getValue().equals(STEP_RECODRING);
    }

    public void isTagsActive() {
        areTagsActive.setValue(true);
    }

    public void isTagsInactive() {
        areTagsActive.setValue(false);
    }

    public MutableLiveData<String> getStep() {
        return stepRecord;
    }

    public void startRecord() {
        if (!stepRecord.getValue().equals(STEP_RECODRING))
            stepRecord.setValue(STEP_RECODRING);
        if (areTagsActive.getValue() != null && areTagsActive.getValue().booleanValue()) {
            isLiveRecording.setValue(true);
        }
        addActiveLive();
    }

    public void stop() {
        stepRecord.setValue(STEP_STOP);
        areTagsActive.setValue(false);
        isLiveRecording.setValue(false);
        addActiveTags();
        addActiveLive();

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

    public void delete() {
        stepRecord.setValue(STEP_DELETE);
        sessionRepository.remove(session.getValue().getId());
        //TODO sur firebase proposer un clean des données "mortes" ex: si la 1er partie de l'enregistrement a été effectué mais pas de video
        // TODO: idem pour les videos supprimées du device
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
        if (tagSet.getValue() != null && getVideoTime().getValue() != null) {
            TemplateModel template = tagSet.getValue().getTemplates().get(position);
            TagModel newTag = TagModel.createFromTemplate(template);
            newTag.setTaggerId(userId);
            newTag.setSessionId(getSessionId());
            newTag.setStart((int) Math.max(0, getVideoTime().getValue() / Constants.UNIT_TO_MILLI_FACTOR - template.getLeftOffset()));
            newTag.setEnd((int) (getVideoTime().getValue() / Constants.UNIT_TO_MILLI_FACTOR + template.getRightOffset()));
            tagRepository.push(newTag);
            template.incrCount();
            template.setTouch(true);
            return true;
        } else return false;
    }

    public MutableLiveData<List<TagModel>> getTags() {
        if (tags == null) {
            tags = new MutableLiveData<>();
            loadTags();
        }
        return tags;
    }

    public void addActiveTags() {
        SessionModel sessionModel = session.getValue();
        sessionModel.setTagsRecording(areTagsActive.getValue());
        sessionRepository.put(sessionModel);
    }

    public void addActiveLive() {
        if (isLiveRecording.getValue() != null) {
            SessionModel sessionModel = session.getValue();
            sessionModel.setLiveRecording(isLiveRecording.getValue());
            sessionRepository.put(sessionModel);
        }
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
}
