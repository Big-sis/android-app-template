package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class PlayVideoViewModelFactory implements ViewModelProvider.Factory {

    private final String company;
    private final String sessionId;

    public PlayVideoViewModelFactory(String company, String sessionId) {
        this.company = company;
        this.sessionId = sessionId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PlayVideoViewModel.class))
            return (T) new PlayVideoViewModel(company, sessionId);
        throw new IllegalArgumentException("Unkowm ViewModel class");
    }
}
