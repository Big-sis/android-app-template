package fr.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class MyVideosViewModelFactory implements ViewModelProvider.Factory {

    private final String userId;
    private final String mAuth;
    private final String idAndroid;

    public MyVideosViewModelFactory(String userId, String mAuth, String idAndroid) {
        this.userId = userId;
        this.mAuth = mAuth;
        this.idAndroid = idAndroid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MyVideosViewModel.class))
            return (T) new MyVideosViewModel(userId, mAuth, idAndroid);
        throw new IllegalArgumentException("Unkowm ViewModel class");
    }
}
