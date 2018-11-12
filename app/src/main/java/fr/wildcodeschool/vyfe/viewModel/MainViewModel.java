package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import fr.wildcodeschool.vyfe.model.LicenceModel;
import fr.wildcodeschool.vyfe.repository.FirebaseDatabaseRepository;
import fr.wildcodeschool.vyfe.repository.LicenceRepository;

public class MainViewModel extends ViewModel {
    LicenceRepository licenseRepository;
    private MutableLiveData<List<LicenceModel>> license;

    public MainViewModel(String idUser) {
        licenseRepository = new LicenceRepository(idUser);
    }

    public LiveData<List<LicenceModel>> getLicence( MainViewModel.ForecastResponse listener) {
        if (license == null) {
            license = new MutableLiveData<List<LicenceModel>>();
            loadLicense(listener);
        }
        return license;
    }

//TODO voir quelle methode garder interface ou  observer
    public void loadLicense(final MainViewModel.ForecastResponse listener) {
        licenseRepository.addListener(new FirebaseDatabaseRepository.CallbackInterface<LicenceModel>() {
            @Override
            public void onSuccess(List<LicenceModel> result) {
                license.setValue(result);
                listener.onSuccess(license);
            }

            @Override
            public void onError(Exception e) {
                license.setValue(null);
                listener.onError(e.getMessage());
            }
        });
    }

    public interface ForecastResponse {

        void onSuccess(LiveData<List<LicenceModel>> license);

        void onError(String error);


    }
}

