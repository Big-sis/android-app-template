package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import fr.vyfe.model.SessionModel;
import fr.vyfe.repository.BaseListValueEventListener;
import fr.vyfe.repository.UserRepository;


public class MyVideosViewModel extends ViewModel {

    private UserRepository repository;
    private MutableLiveData<List<SessionModel>> sessions;

    public MyVideosViewModel(String userId) {
        repository = new UserRepository(userId);
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
        repository.removeListeners();
    }

    private void loadSessions() {

        repository.addListListener(new BaseListValueEventListener.CallbackInterface<SessionModel>() {
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
