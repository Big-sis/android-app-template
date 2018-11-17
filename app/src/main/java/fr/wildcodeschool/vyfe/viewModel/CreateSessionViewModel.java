package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.model.TagSetModel;
import fr.wildcodeschool.vyfe.repository.BaseListValueEventListener;
import fr.wildcodeschool.vyfe.repository.FirebaseDatabaseRepository;
import fr.wildcodeschool.vyfe.repository.TagSetRepository;


public class CreateSessionViewModel extends ViewModel {

    private TagSetRepository repository;
    private SessionModel session;
    private MutableLiveData<List<TagSetModel>> tagSets;

    public CreateSessionViewModel(String userId, String companyId) {
        repository = new TagSetRepository(userId, companyId);
    }

    public SessionModel getSession() {
        if (session == null) {
            session = new SessionModel();
        }
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
        repository.removeListeners();
    }

    private void loadTagSets() {
        repository.addListListener(new BaseListValueEventListener.CallbackInterface<TagSetModel>() {
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
