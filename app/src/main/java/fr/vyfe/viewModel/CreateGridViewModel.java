package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import fr.vyfe.model.TagSetModel;
import fr.vyfe.repository.TagSetRepository;


public class CreateGridViewModel extends ViewModel {

    private MutableLiveData<TagSetModel> tagSet;
    private TagSetRepository repository;

    public CreateGridViewModel(String userId, String companyId) {
        repository = new TagSetRepository(userId, companyId);
        tagSet = new MutableLiveData<>();
    }

    public LiveData<TagSetModel> getTagSet(){
        return tagSet;
    }

    @Override
    protected void onCleared() {
        repository.removeListeners();
    }
}
