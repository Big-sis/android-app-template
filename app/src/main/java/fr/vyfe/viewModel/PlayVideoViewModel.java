package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import fr.vyfe.model.SessionModel;
import fr.vyfe.repository.SessionRepository;


public class PlayVideoViewModel extends ViewModel {

    private SessionModel session;
    private SessionRepository sessionRepository;
    private MutableLiveData<Integer> videoPosition;
    private MutableLiveData<Boolean> isPlaying;

    public PlayVideoViewModel(String company) {
        sessionRepository = new SessionRepository(company);
        isPlaying = new MutableLiveData<>();
        isPlaying.setValue(false);
        videoPosition = new MutableLiveData<>();
        videoPosition.setValue(0);
    }

    public void init(SessionModel session) {
        this.session = session;
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

    public SessionModel getSession(){
        return session;
    }

    @Override
    protected void onCleared() {
        sessionRepository.removeListeners();
    }
}
