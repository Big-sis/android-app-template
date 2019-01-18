package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.vyfe.Constants;
import fr.vyfe.helper.AuthHelper;
import fr.vyfe.model.SessionModel;
import fr.vyfe.repository.BaseListValueEventListener;
import fr.vyfe.repository.SessionRepository;

import static android.os.Environment.DIRECTORY_MOVIES;
import static android.os.Environment.getExternalStoragePublicDirectory;


public class MyVideosViewModel extends VyfeViewModel {

    private SessionRepository repository;
    private MutableLiveData<List<SessionModel>> sessions;
    private String filter;
    private MutableLiveData<Boolean> permissions;
    private static String mAuth;

    public MyVideosViewModel(String companyId, String androidId, String mAuth) {
        repository = new SessionRepository(companyId);
        repository.setOrderByChildKey("idAndroid");
        repository.setEqualToKey(androidId);
        filter = "";
        permissions = new MutableLiveData<>();
        permissions.setValue(false);
        this.mAuth = mAuth;
    }

    public void permissionsAccepted() {
        permissions.setValue(true);
    }

    public MutableLiveData<Boolean> getPermissions() {
        return permissions;
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


                File externalStorage = getExternalStoragePublicDirectory(DIRECTORY_MOVIES + "/" + Constants.VIDEO_DIRECTORY_NAME);
                final String racineExternalStorage = String.valueOf(externalStorage.getAbsoluteFile());
                final String[] filesExternalStorage = externalStorage.list();

                ArrayList<SessionModel> filtered = new ArrayList<>();
                if (filesExternalStorage != null) {
                    for (String nameFileExternalStorage : filesExternalStorage) {
                        String nameCache = racineExternalStorage + "/" + nameFileExternalStorage;
                        for (SessionModel session : result) {
                            if (session.getName() != null && session.getName().contains(filter) && session.getDeviceVideoLink().equals(nameCache))
                                filtered.add(session);
                        }
                    }
                }
                // For moment,to save time,"Index Your Data" is the "future"
                for(int i =0; i< filtered.size();i++){
                    if(!filtered.get(i).getAuthor().equals(mAuth)){
                        filtered.remove(i);
                }

                }
                //TODO: respository is filtered by idAndroid
                // for moment, to save time, second filter is here
                Collections.sort(filtered, new Comparator<SessionModel>() {
                    @Override
                    public int compare(SessionModel o1, SessionModel o2) {
                        return (int) (o2.getDate().getTime() - o1.getDate().getTime());
                    }
                });

                sessions.setValue(filtered);
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
