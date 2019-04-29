package fr.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class CreateGridViewModelFactory implements ViewModelProvider.Factory {

    private final String userId;
    private final String companyId;
    private final String displayName;

    public CreateGridViewModelFactory(String userId, String displayName, String companyId) {
        this.userId = userId;
        this.companyId = companyId;
        this.displayName = displayName;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CreateGridViewModel.class))
            return (T) new CreateGridViewModel(userId,displayName, companyId);
        throw new IllegalArgumentException("Unkowm ViewModel class");
    }
}
