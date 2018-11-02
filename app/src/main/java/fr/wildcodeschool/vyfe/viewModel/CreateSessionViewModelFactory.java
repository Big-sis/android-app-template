package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class CreateSessionViewModelFactory implements ViewModelProvider.Factory {

    private final String userId;

    public CreateSessionViewModelFactory(String userId) {
        this.userId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CreateSessionViewModel.class))
            return (T) new CreateSessionViewModel(userId);
        throw new IllegalArgumentException("Unkowm ViewModel class");
    }
}
