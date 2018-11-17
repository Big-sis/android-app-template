package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.repository.BaseSingleValueEventListener;
import fr.wildcodeschool.vyfe.repository.FirebaseDatabaseRepository;
import fr.wildcodeschool.vyfe.repository.FirebaseDatabaseRepositorySingle;
import fr.wildcodeschool.vyfe.repository.SessionRepository;

public class InfoVideoViewModel extends ViewModel {
    public SessionRepository sessionRepository;
    private MutableLiveData<SessionModel> session;
    private String sessionId;


    public InfoVideoViewModel(String companyId, String sessionId) {
        sessionRepository = new SessionRepository(companyId);
        this.sessionId = sessionId;
    }

    public LiveData<SessionModel> getSession() {
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

    private void loadSession() {
        if (session == null) {
            session = new MutableLiveData<SessionModel>();
        }
        sessionRepository.addChildListener(sessionId, new BaseSingleValueEventListener.CallbackInterface<SessionModel>() {
            @Override
            public void onSuccess(SessionModel result) {
                session.setValue(result);
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }


}
