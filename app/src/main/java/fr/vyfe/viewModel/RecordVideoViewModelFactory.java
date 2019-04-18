package fr.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class RecordVideoViewModelFactory implements ViewModelProvider.Factory {

    private final String companyId;
    private final String userId;
    private final String sessionId;


    public RecordVideoViewModelFactory(String companyId, String userId, String sessionId) {
        this.companyId = companyId;
        this.userId = userId;
        this.sessionId = sessionId;

    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecordVideoViewModel.class))
            return (T) new RecordVideoViewModel(companyId, userId, sessionId);
        throw new IllegalArgumentException("Unkowm ViewModel class");
    }
}
