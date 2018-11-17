package fr.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class CreateGridViewModelFactory implements ViewModelProvider.Factory {

    private final String userId;
    private final String companyId;

    public CreateGridViewModelFactory(String userId, String companyId) {
        this.userId = userId;
        this.companyId = companyId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PlayVideoViewModel.class))
            return (T) new CreateGridViewModel(userId, companyId);
        throw new IllegalArgumentException("Unkowm ViewModel class");
    }
}
