package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.model.TagSetModel;
import fr.wildcodeschool.vyfe.repository.FirebaseDatabaseRepository;
import fr.wildcodeschool.vyfe.repository.TagSetRepository;
import fr.wildcodeschool.vyfe.repository.UserSessionRepository;


public class MyVideosViewModel extends ViewModel {

    private UserSessionRepository repository;
    private MutableLiveData<List<SessionModel>> sessions;

    public MyVideosViewModel(String userId) {
        repository = new UserSessionRepository(userId);
    }

    public LiveData<List<SessionModel>> getSessions() {
        if (sessions == null) {
            sessions = new MutableLiveData<>();
            loadSessions();
        }
        return sessions;
    }

    @Override
    protected void onCleared() {
        repository.removeListener();
    }

    private void loadSessions() {

        repository.addListener(new FirebaseDatabaseRepository.CallbackInterface<SessionModel>() {
            @Override
            public void onSuccess(List<SessionModel> result) {
                sessions.setValue(result);
            }

            @Override
            public void onError(Exception e) {
                sessions.setValue(null);
            }
        });

    }


}
