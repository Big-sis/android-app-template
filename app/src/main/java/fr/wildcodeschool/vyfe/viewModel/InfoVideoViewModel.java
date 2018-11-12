package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.repository.FirebaseDatabaseRepositorySingle;
import fr.wildcodeschool.vyfe.repository.SessionRepository;

public class InfoVideoViewModel extends ViewModel {
    public SessionRepository sessionRepository;
    private MutableLiveData<SessionModel> session;


    public InfoVideoViewModel(String userId, String sessionId) {
        sessionRepository = new SessionRepository(userId, sessionId);
    }

    public LiveData<SessionModel> getSession() {
        if (session == null) {
            session = new MutableLiveData<SessionModel>();
            loadSession();
        }
        return session;
    }

    /** utilile?
     @Override
     protected void onCleared() {
     sessionRepository.removeListener();
     }**/

    public void loadSession() {
        if (session == null) {
            session = new MutableLiveData<SessionModel>();
        }
        sessionRepository.addListener(new FirebaseDatabaseRepositorySingle.CallbackInterface<SessionModel>() {
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
