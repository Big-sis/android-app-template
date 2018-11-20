package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import fr.vyfe.model.SessionModel;
import fr.vyfe.repository.BaseListValueEventListener;
import fr.vyfe.repository.SessionRepository;


public class MyVideosViewModel extends ViewModel {

    private SessionRepository repository;
    private MutableLiveData<List<SessionModel>> sessions;
    private String filter;

    public MyVideosViewModel(String companyId, String androidId) {
        repository = new SessionRepository(companyId);
        repository.setOrderByChildKey("idAndroid");
        repository.setEqualToKey(androidId);
        filter = "";
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
                for (SessionModel session: result) {
                    if (!session.getName().contains(filter))
                        result.remove(session);
                }
                sessions.setValue(result);
            }

            @Override
            public void onError(Exception e) {
                sessions.setValue(null);
            }
        });

    }

    public void setFilter(String filter) {
        this.filter = filter;
        loadSessions();
    }
}
