package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    private final String userId;

    public MainViewModelFactory(String userId) {
        this.userId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class))
            return (T) new MainViewModel(userId);
        throw new IllegalArgumentException("Unkowm ViewModel class");
    }
}
