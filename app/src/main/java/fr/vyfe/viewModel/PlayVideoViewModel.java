package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import fr.vyfe.repository.SessionRepository;
import fr.vyfe.repository.TagSetRepository;


public class PlayVideoViewModel extends VyfeViewModel {

    private MutableLiveData<Integer> videoPosition;
    private MutableLiveData<Boolean> isPlaying;

    public PlayVideoViewModel(String companyId, String userId) {
        sessionRepository = new SessionRepository(companyId);
        tagSetRepository = new TagSetRepository(userId, companyId);
        isPlaying = new MutableLiveData<>();
        videoPosition = new MutableLiveData<>();
    }

    public void init(String sessionId) {
        this.sessionId = sessionId;
        isPlaying.setValue(false);
        videoPosition.setValue(0);
    }

    public LiveData<Integer> getVideoPosition() {
        return videoPosition;
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
}
