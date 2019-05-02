package fr.vyfe.viewModel;


import android.arch.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;

import fr.vyfe.model.SessionModel;
import fr.vyfe.repository.SessionRepository;

public class EditSessionViewModel extends VyfeViewModel {
    private String newDescription;
    private String newName;
    private MutableLiveData<Boolean> hasDataChanged;


    public EditSessionViewModel(String companyId) {
        sessionRepository = new SessionRepository(companyId);
        hasDataChanged = new MutableLiveData<>();
        hasDataChanged.setValue(false);
    }

    public void init(String sessionId) {
        loadSession(sessionId);
    }

    public Task<Void> deleteSession() {
        return sessionRepository.remove(session.getValue().getId());
    }

    public void setNewDescription(String newDescription) {
        this.newDescription = newDescription;
        hasDataChanged.setValue(true);
    }

    public String getNewName() {
        return this.newName;

    }

    public void setNewName(String newName) {
        this.newName = newName;
        hasDataChanged.setValue(true);
    }

    public Task<Void> updateSession() {
        SessionModel session = this.session.getValue();
        if (this.newName != null) session.setName(this.newName);
        if (this.newDescription != null) session.setDescription(this.newDescription);
        return sessionRepository.update(session);
    }

    public Task<Void> deleteLinkAppSession() {
        SessionModel sessionModel = session.getValue();
        sessionModel.setDeviceVideoLink(null);
        return sessionRepository.update(sessionModel);
    }

    @Override
    public void onCleared() {
        sessionRepository.removeListeners();
    }

    public MutableLiveData<Boolean> watchDataChange() {
        return hasDataChanged;
    }
}
