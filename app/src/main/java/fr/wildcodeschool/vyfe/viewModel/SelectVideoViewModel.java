package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import fr.wildcodeschool.vyfe.helper.InternetConnexionHelper;
import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.model.VimeoTokenModel;
import fr.wildcodeschool.vyfe.repository.FirebaseDatabaseRepositorySingle;
import fr.wildcodeschool.vyfe.repository.SessionRepository;
import fr.wildcodeschool.vyfe.repository.VimeoTokenRepository;


public class SelectVideoViewModel extends ViewModel {
    public SessionRepository sessionRepository;
    private MutableLiveData<SessionModel> session;

    private VimeoTokenRepository vimeoTokenRepository;
    private MutableLiveData<VimeoTokenModel> vimeoToken;

    private MutableLiveData<Boolean> haveInternetConnexion;


    public SelectVideoViewModel(String userId, String sessionId) {
        sessionRepository = new SessionRepository(userId, sessionId);
        vimeoTokenRepository = new VimeoTokenRepository();
        haveInternetConnexion = new MutableLiveData<>();
    }

    public LiveData<SessionModel> getSession() {
        if (session == null) {
            session = new MutableLiveData<SessionModel>();
            loadSession();
        }
        return session;
    }

    public LiveData<VimeoTokenModel> getVimeoToken() {
        if (vimeoToken == null) {
            vimeoToken = new MutableLiveData<>();
            loadToken();
        }
        return vimeoToken;
    }

    public LiveData<Boolean> getHaveInternetConnexion(Context context) {
        haveInternetConnexion.setValue(InternetConnexionHelper.haveInternetConnection(context));
        return haveInternetConnexion;
    }


    /**
     * //TODO utilité?
     *
     * @Override protected void onCleared() {
     * sessionRepository.removeListener();
     * }
     **/

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

    public void loadToken() {
        if (vimeoToken == null) {
            vimeoToken = new MutableLiveData<>();
        }
        vimeoTokenRepository.addListener(new FirebaseDatabaseRepositorySingle.CallbackInterface<VimeoTokenModel>() {
            @Override
            public void onSuccess(VimeoTokenModel result) {
                vimeoToken.setValue(result);
            }

            @Override
            public void onError(Exception e) {
                vimeoToken.setValue(null);
            }
        });
    }

    public void save() {
        sessionRepository.createVimeoLink(this.session.getValue().getVideoLink());
    }
}
