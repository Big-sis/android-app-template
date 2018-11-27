package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import java.util.List;

import fr.vyfe.helper.InternetConnexionHelper;
import fr.vyfe.model.VimeoTokenModel;
import fr.vyfe.repository.BaseListValueEventListener;
import fr.vyfe.repository.FirebaseDatabaseRepository;
import fr.vyfe.repository.SessionRepository;
import fr.vyfe.repository.VimeoTokenRepository;
import fr.vyfe.model.SessionModel;
import fr.vyfe.repository.BaseSingleValueEventListener;


public class SelectVideoViewModel extends ViewModel {
    public SessionRepository sessionRepository;
    private MutableLiveData<SessionModel> session;
    private String sessionId;

    private VimeoTokenRepository vimeoTokenRepository;
    private MutableLiveData<VimeoTokenModel> vimeoToken;

    private MutableLiveData<Boolean> haveInternetConnexion;


    public SelectVideoViewModel(String companyId, String sessionId) {
        vimeoTokenRepository = new VimeoTokenRepository(companyId);
        haveInternetConnexion = new MutableLiveData<>();
        this.sessionId = sessionId;
        sessionRepository = new SessionRepository(companyId);
    }

    public LiveData<SessionModel> getSession() {
        if (session == null) {
            session = new MutableLiveData<>();
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

    @Override
    protected void onCleared() {
        sessionRepository.removeListeners();
    }

    public void loadSession() {
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

    public void loadToken() {
        if (vimeoToken == null) {
            vimeoToken = new MutableLiveData<>();
        }
        vimeoTokenRepository.addListListener(new BaseListValueEventListener.CallbackInterface<VimeoTokenModel>() {
            @Override
            public void onSuccess(List<VimeoTokenModel> result) {
                vimeoToken.setValue(result.get(0));
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
