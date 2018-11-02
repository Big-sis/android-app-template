package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class SelectVideoViewModelFactory implements ViewModelProvider.Factory {

    private final String userId;
    private final String sessionId;
    private final String tagSetId;

    public SelectVideoViewModelFactory(String userId, String sessionId, String tagSetId) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.tagSetId = tagSetId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SelectVideoViewModel.class))
            return (T) new SelectVideoViewModel(userId, sessionId,tagSetId);
        throw new IllegalArgumentException("Unkowm ViewModel class");
    }
}
