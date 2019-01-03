package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import fr.vyfe.model.TagModel;
import fr.vyfe.repository.BaseListValueEventListener;
import fr.vyfe.repository.SessionRepository;
import fr.vyfe.repository.TagRepository;
import fr.vyfe.repository.TagSetRepository;


public class PlayVideoViewModel extends VyfeViewModel {

    private MutableLiveData<Integer> videoPosition;
    private MutableLiveData<Boolean> isPlaying;
    private TagRepository tagRepository;
    private MutableLiveData<List<TagModel>> tags;

    public PlayVideoViewModel(String companyId, String userId, String sessionId) {
        sessionRepository = new SessionRepository(companyId);
        tagSetRepository = new TagSetRepository(userId, companyId);
        tagRepository = new TagRepository(companyId, userId, sessionId);
        isPlaying = new MutableLiveData<>();
        videoPosition = new MutableLiveData<>();
        this.sessionId = sessionId;
    }

    public void init() {
        isPlaying.setValue(false);
        this.videoPosition.setValue(0);
    }

    public LiveData<Integer> getVideoPosition() {
        return this.videoPosition;
    }

    public void setVideoPosition(Integer videoPosition) {
        this.videoPosition.setValue(videoPosition);
    }

    public LiveData<Boolean> isPlaying(){
        return isPlaying;
    }

    public void play(){
        isPlaying.setValue(true);
    }

    public void pause(){
        isPlaying.setValue(false);
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
}
