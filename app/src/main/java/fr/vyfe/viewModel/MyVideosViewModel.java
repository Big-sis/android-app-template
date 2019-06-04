package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import fr.vyfe.Constants;
import fr.vyfe.model.SessionModel;
import fr.vyfe.repository.BaseListValueEventListener;
import fr.vyfe.repository.SessionRepository;

import static android.os.Environment.DIRECTORY_MOVIES;
import static android.os.Environment.getExternalStoragePublicDirectory;


public class MyVideosViewModel extends VyfeViewModel {

    private static String mAuth;
    private SessionRepository repository;
    private MutableLiveData<List<SessionModel>> sessions;
    private String filter;
    private MutableLiveData<Boolean> permissions;
    private String androidId;
    private HashMap<SessionModel, Boolean> mSelectedSessions;
    private MutableLiveData<Boolean> isCancel;


    public MyVideosViewModel(String companyId, String mAuth, String androidId) {
        this.mAuth = mAuth;
        repository = new SessionRepository(companyId);
        repository.setOrderByChildKey("owner/uid");
        repository.setEqualToKey(mAuth);
        //TODO a verifier

        filter = "";
        permissions = new MutableLiveData<>();
        permissions.setValue(false);
        this.androidId = androidId;
        isCancel = new MutableLiveData<>();
        isCancel.setValue(true);
    }

    public void initSelectedSession() {

        mSelectedSessions = new HashMap<>();
        for (SessionModel sessionModel : sessions.getValue()) {
            mSelectedSessions.put(sessionModel,false);
        }
    }

    public void selectedSession(SessionModel sessionModel){
        if (!mSelectedSessions.get(sessionModel).booleanValue()){
            mSelectedSessions.put(sessionModel,true);
        }

        else {
            mSelectedSessions.remove(sessionModel);
            mSelectedSessions.put(sessionModel,false);
        }

    }
    public MutableLiveData<Boolean> isCancelSelection(){
        return isCancel;
    }
    public void cancelSelected(){
        isCancel.setValue(true);
    }

    public void inProgress(){
        isCancel.setValue(false);
    }


    public void longSelectedSession(SessionModel sessionModel){
         mSelectedSessions.remove(sessionModel);
            mSelectedSessions.put(sessionModel,true);

    }
    public HashMap<SessionModel, Boolean> getmSelectedSessions() {
        return mSelectedSessions;
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

    public Task<Void> deleteSession(String id) {
        return repository.remove(id);
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

                            //Recup video sur la tablette
                            if (session.getName() != null &&
                                    session.getName().contains(filter) &&
                                    session.getDeviceVideoLink() != null &&
                                    session.getDeviceVideoLink().equals(nameCache))
                                filtered.add(session);
                        }
                    }

                    for (SessionModel session : result) {
                        //recupe les videos sur vimeo et pas sur la tablette
                        if (session.getName() != null && session.getName().contains(filter) &&
                                session.getDeviceVideoLink() == null &&
                                session.getServerVideoLink() != null)
                            filtered.add(session);

                        //recupere videos sur vimeo dune autre tablette
                        if (session.getDeviceVideoLink()!=null&&
                                !androidId.equals(session.getIdAndroid()) &&
                                session.getServerVideoLink() != null)
                            filtered.add(session);
                    }
                //TODO recuperer les vidéos partagées

                }

                //TODO: respository is filtered by Author
                // for moment, to save time, second filter is here


                //TODO: Filter data server side
                // for now, to save time, second filter is here

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
