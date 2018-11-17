package fr.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class MyVideosViewModelFactory implements ViewModelProvider.Factory {

    private final String userId;

    public MyVideosViewModelFactory(String userId) {
        this.userId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MyVideosViewModel.class))
            return (T) new MyVideosViewModel(userId);
        throw new IllegalArgumentException("Unkowm ViewModel class");
    }
}
