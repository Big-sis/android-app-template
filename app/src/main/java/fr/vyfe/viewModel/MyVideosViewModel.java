package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.vyfe.model.SessionModel;
import fr.vyfe.repository.BaseListValueEventListener;
import fr.vyfe.repository.SessionRepository;

import static android.os.Environment.DIRECTORY_MOVIES;
import static android.os.Environment.getExternalStoragePublicDirectory;


public class MyVideosViewModel extends VyfeViewModel {

    private SessionRepository repository;
    private MutableLiveData<List<SessionModel>> sessions;
    private String filter;

    public MyVideosViewModel(String companyId, String androidId) {
        repository = new SessionRepository(companyId);
        repository.setOrderByChildKey("idAndroid");
        repository.setEqualToKey(androidId);
        filter = "";
    }

    public LiveData<List<SessionModel>> getSessions() {
        if (sessions == null) {
            sessions = new MutableLiveData<>();
            loadSessions();
        }
        return sessions;
    }

    @Override
    protected void onCleared() {
        repository.removeListeners();
    }

    private void loadSessions() {

        repository.addListListener(new BaseListValueEventListener.CallbackInterface<SessionModel>() {
            @Override
            public void onSuccess(List<SessionModel> result) {
            //TODO: mettre les autorisations en placent
                File externalStorage = getExternalStoragePublicDirectory(DIRECTORY_MOVIES + "/" + "Vyfe");
                final String racineExternalStorage = String.valueOf(externalStorage.getAbsoluteFile());
                final String[] filesExternalStorage = externalStorage.list();

                ArrayList<SessionModel> filtered = new ArrayList<>();
                assert filesExternalStorage != null;
                for (String nameFileExternalStorage : filesExternalStorage) {
                    String nameCache = racineExternalStorage + "/" + nameFileExternalStorage;
                    for (SessionModel session : result) {
                        if (session.getName().contains(filter) && session.getDeviceVideoLink().equals(nameCache))
                            filtered.add(session);
                    }

                } sessions.setValue(filtered);

            }

            @Override
            public void onError(Exception e) {
                sessions.setValue(null);
            }
        });

    }

    public void setFilter(String filter) {
        this.filter = filter;
        loadSessions();
    }


}
