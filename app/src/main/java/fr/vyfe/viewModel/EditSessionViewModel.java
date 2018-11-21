package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;

import fr.vyfe.model.SessionModel;
import fr.vyfe.repository.BaseSingleValueEventListener;
import fr.vyfe.repository.SessionRepository;

public class EditSessionViewModel extends ViewModel {
    public SessionRepository sessionRepository;
    private MutableLiveData<SessionModel> session;
    private String newDescription;
    private String newName;


    public EditSessionViewModel(String companyId) {
        sessionRepository = new SessionRepository(companyId);
    }


    public void init(String sessionId) {
        loadSession(sessionId);
    }

    public LiveData<SessionModel> getSession() {
        return session;
    }

    @Override
    protected void onCleared() {
        sessionRepository.removeListeners();
    }

    private void loadSession(String sessionId) {
        if (session == null) {
            session = new MutableLiveData<>();
        }
        sessionRepository.addChildListener(sessionId, new BaseSingleValueEventListener.CallbackInterface<SessionModel>() {
            @Override
            public void onSuccess(SessionModel result) {
                session.setValue(result);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    public Task<Void> deleteSession() {
        return sessionRepository.remove(session.getValue().getId());
    }

    public void setNewDescription(String newDescription) {
        this.newDescription = newDescription;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public Task<Void> editSession() {
        SessionModel sessionModel = session.getValue();
        sessionModel.setName(this.newName);
        sessionModel.setDescription(this.newDescription);
        return sessionRepository.put(sessionModel);
    }
}
