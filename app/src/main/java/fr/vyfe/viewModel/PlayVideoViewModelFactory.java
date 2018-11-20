package fr.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class PlayVideoViewModelFactory implements ViewModelProvider.Factory {

    private final String userId;
    private final String sessionId;

    public PlayVideoViewModelFactory(String userId, String sessionId) {
        this.userId = userId;
        this.sessionId = sessionId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PlayVideoViewModel.class))
            return (T) new PlayVideoViewModel(userId, sessionId);
        throw new IllegalArgumentException("Unkowm ViewModel class");
    }
}
