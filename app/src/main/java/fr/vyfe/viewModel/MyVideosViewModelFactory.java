package fr.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class MyVideosViewModelFactory implements ViewModelProvider.Factory {

    private final String userId;
    private final String androidId;
    private final String mAuth;

    public MyVideosViewModelFactory(String userId, String androidId, String mAuth) {
        this.userId = userId;
        this.androidId = androidId;
        this.mAuth =mAuth;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MyVideosViewModel.class))
            return (T) new MyVideosViewModel(userId, androidId,mAuth);
        throw new IllegalArgumentException("Unkowm ViewModel class");
    }
}
