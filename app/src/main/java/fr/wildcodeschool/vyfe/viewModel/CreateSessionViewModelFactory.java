package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class CreateSessionViewModelFactory implements ViewModelProvider.Factory {

    private final String userId;
    private final String companyId;

    public CreateSessionViewModelFactory(String userId, String companyId) {
        this.userId = userId;
        this.companyId = companyId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CreateSessionViewModel.class))
            return (T) new CreateSessionViewModel(userId, companyId);
        throw new IllegalArgumentException("Unkowm ViewModel class");
    }
}
