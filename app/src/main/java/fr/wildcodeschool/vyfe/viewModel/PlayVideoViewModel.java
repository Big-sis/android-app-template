package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.model.TagModel;
import fr.wildcodeschool.vyfe.repository.FirebaseDatabaseRepositorySingle;
import fr.wildcodeschool.vyfe.repository.SessionRepository;


//TODO BDD2
public class PlayVideoViewModel extends ViewModel {

    private MutableLiveData<SessionModel> session;
    private SessionRepository sessionRepository;
    private MutableLiveData<Integer> videoPosition;
    private MutableLiveData<Boolean> isPlaying;

    public PlayVideoViewModel(String company, String sessionId) {
        sessionRepository = new SessionRepository(company, sessionId);
        isPlaying = new MutableLiveData<Boolean>();
        isPlaying.setValue(false);
        videoPosition = new MutableLiveData<Integer>();
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

    public LiveData<SessionModel> getSession(){
        return session;
    }

    @Override
    protected void onCleared() {
        sessionRepository.removeListener();
    }

    public void loadSession(){
        if (session == null) {
            session = new MutableLiveData<SessionModel>();
        }
        sessionRepository.addListener(new FirebaseDatabaseRepositorySingle.CallbackInterface<SessionModel>() {
            @Override
            public void onSuccess(SessionModel result) { session.setValue(result); }

            @Override
            public void onError(Exception e) { session.setValue(null); }
        });

    }
}
