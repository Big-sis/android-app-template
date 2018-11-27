package fr.vyfe.viewModel;

import fr.vyfe.repository.SessionRepository;
import fr.vyfe.repository.TagSetRepository;
import fr.vyfe.helper.InternetConnexionHelper;
import fr.vyfe.model.VimeoTokenModel;
import fr.vyfe.repository.VimeoTokenRepository;


public class SelectVideoViewModel extends VyfeViewModel {
  
  private VimeoTokenRepository vimeoTokenRepository;
    private MutableLiveData<VimeoTokenModel> vimeoToken;

    private MutableLiveData<Boolean> haveInternetConnexion;

    public SelectVideoViewModel(String companyId, String userId) {
        vimeoTokenRepository = new VimeoTokenRepository(companyId);
        haveInternetConnexion = new MutableLiveData<>();
        sessionRepository = new SessionRepository(companyId);
        tagSetRepository = new TagSetRepository(userId, companyId);
    }

    public void init(String sessionId) {
        this.sessionId = sessionId;
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
