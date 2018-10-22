package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import fr.wildcodeschool.vyfe.model.SessionModelBDD2;
import fr.wildcodeschool.vyfe.model.TagModel;
import fr.wildcodeschool.vyfe.repository.FirebaseDatabaseRepositorySingle;
import fr.wildcodeschool.vyfe.repository.SessionRepositoryBDD2;


//TODO BDD2
public class PlayVideoViewModel extends ViewModel {

    private MutableLiveData<List<TagModel>> tags;
    private MutableLiveData<SessionModelBDD2> session;
    private SessionRepositoryBDD2 sessionRepository;
    private MutableLiveData<Integer> videoPosition;
    private MutableLiveData<Boolean> isPlaying;

    public PlayVideoViewModel(String company, String sessionId) {
        sessionRepository = new SessionRepositoryBDD2(company, sessionId);
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

    public LiveData<SessionModelBDD2> getSession(){
        return session;
    }

    @Override
    protected void onCleared() {
        sessionRepository.removeListener();
    }

    public void loadSession(String id){
        if (session == null) {
            session = new MutableLiveData<SessionModelBDD2>();
        }
        sessionRepository.addListener(new FirebaseDatabaseRepositorySingle.CallbackInterface<SessionModelBDD2>() {
            @Override
            public void onSuccess(SessionModelBDD2 result) { session.setValue(result); }

            @Override
            public void onError(Exception e) { session.setValue(null); }
        });

    }
}
