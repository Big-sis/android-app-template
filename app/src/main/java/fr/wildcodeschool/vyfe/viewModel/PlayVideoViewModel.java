package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.repository.BaseSingleValueEventListener;
import fr.wildcodeschool.vyfe.repository.FirebaseDatabaseRepositorySingle;
import fr.wildcodeschool.vyfe.repository.SessionRepository;


public class PlayVideoViewModel extends ViewModel {

    private MutableLiveData<SessionModel> session;
    private SessionRepository sessionRepository;
    private MutableLiveData<Integer> videoPosition;
    private MutableLiveData<Boolean> isPlaying;
    private String sessionId;

    public PlayVideoViewModel(String company, String sessionId) {
        sessionRepository = new SessionRepository(company);
        isPlaying = new MutableLiveData<Boolean>();
        isPlaying.setValue(false);
        videoPosition = new MutableLiveData<Integer>();
        videoPosition.setValue(0);
        this.sessionId = sessionId;
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

    public LiveData<SessionModel> getSession(){
        if (session == null) {
            session = new MutableLiveData<SessionModel>();
            loadSession();
        }
        return session;
    }

    @Override
    protected void onCleared() {
        sessionRepository.removeListeners();
    }

    private void loadSession(){

        sessionRepository.addChildListener(sessionId, new BaseSingleValueEventListener.CallbackInterface<SessionModel>() {
            @Override
            public void onSuccess(SessionModel result) { session.setValue(result); }

            @Override
            public void onError(Exception e) { session.setValue(null); }
        });

    }
}
