package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.repository.AllUserSessionsRepository;
import fr.wildcodeschool.vyfe.repository.FirebaseDatabaseRepositorySingle;


public class MyVideoViewModel  extends ViewModel {
    private MutableLiveData<SessionModel> session;
    private AllUserSessionsRepository sessionRepository;

    public MyVideoViewModel(String userId) {
        sessionRepository = new AllUserSessionsRepository(userId);
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
