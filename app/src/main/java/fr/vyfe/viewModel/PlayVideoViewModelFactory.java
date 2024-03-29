package fr.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class PlayVideoViewModelFactory implements ViewModelProvider.Factory {

    private final String companyId;
    private final String userId;
    private final String sessionId;
    private final String androidId;

    public PlayVideoViewModelFactory(String companyId, String userId, String sessionId, String androidId) {
        this.companyId = companyId;
        this.userId = userId;
        this.sessionId = sessionId;
        this.androidId = androidId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PlayVideoViewModel.class))
            return (T) new PlayVideoViewModel(companyId, userId, sessionId,androidId);
        throw new IllegalArgumentException("Unkowm ViewModel class");
    }
}
