package fr.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class EditSessionViewModelFactory implements ViewModelProvider.Factory {

    private final String userId;

    public EditSessionViewModelFactory(String userId) {
        this.userId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EditSessionViewModel.class))
            return (T) new EditSessionViewModel(userId);
        throw new IllegalArgumentException("Unkowm ViewModel class");
    }
}
