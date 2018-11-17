package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import fr.vyfe.model.SessionModel;
import fr.vyfe.repository.BaseSingleValueEventListener;
import fr.vyfe.repository.SessionRepository;


public class SelectVideoViewModel extends ViewModel {
    public SessionRepository sessionRepository;
    private MutableLiveData<SessionModel> session;
    private String sessionId;


    public SelectVideoViewModel(String companyId, String sessionId) {
        this.sessionId = sessionId;
        sessionRepository = new SessionRepository(companyId);
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

    public void loadSession() {
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
                session.setValue(null);
            }
        });

    }

}
