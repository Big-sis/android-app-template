package fr.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class SelectVideoViewModelFactory implements ViewModelProvider.Factory {

    private final String companyId;
    private final String userId;

    public SelectVideoViewModelFactory(String companyId, String userId) {
        this.companyId = companyId;
        this.userId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SelectVideoViewModel.class))
            return (T) new SelectVideoViewModel(companyId, userId);
        throw new IllegalArgumentException("Unkowm ViewModel class");
    }
}
