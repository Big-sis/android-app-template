package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.model.TagSetModel;
import fr.wildcodeschool.vyfe.repository.FirebaseDatabaseRepository;
import fr.wildcodeschool.vyfe.repository.FirebaseDatabaseRepositorySingle;
import fr.wildcodeschool.vyfe.repository.SessionRepository;
import fr.wildcodeschool.vyfe.repository.SimpleTagSetRepository;
import fr.wildcodeschool.vyfe.repository.TagSetRepository;

public class SelectVideoViewModel extends ViewModel {
    private MutableLiveData<SessionModel> session;
    public SessionRepository sessionRepository;

    //TODO ici recuperer qu'un TagSetModel pas besoin de recuperer une liste
    private MutableLiveData<List<TagSetModel>> tagSets;
    public SimpleTagSetRepository repository;



    public SelectVideoViewModel(String userId, String sessionId, String tagSetId ){
        sessionRepository = new SessionRepository(userId,sessionId);
        repository = new SimpleTagSetRepository(userId,tagSetId);
    }

    public LiveData<SessionModel> getSession(){
        loadSession();
        return session;
    }


    public LiveData<List<TagSetModel>> getTagSets() {
        if (tagSets == null) {
            tagSets = new MutableLiveData<>();
            loadTagSets();
        }

        return tagSets;
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

    public void loadTagSets() {

        repository.addListener(new FirebaseDatabaseRepository.CallbackInterface<TagSetModel>() {
            @Override
            public void onSuccess(List<TagSetModel> result) {
                tagSets.setValue(result);
            }

            @Override
            public void onError(Exception e) {
                tagSets.setValue(null);
            }
        });

    }
}
