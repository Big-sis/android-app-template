package fr.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class RecordVideoViewModelFactory implements ViewModelProvider.Factory {

    private final String companyId;

    public RecordVideoViewModelFactory(String companyId) {
        this.companyId = companyId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecordVideoViewModel.class))
            return (T) new RecordVideoViewModel(companyId);
        throw new IllegalArgumentException("Unkowm ViewModel class");
    }
}
