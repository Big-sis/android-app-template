package fr.vyfe.viewModel;

import android.arch.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;

import fr.vyfe.model.SessionModel;
import fr.vyfe.repository.SessionRepository;

public class EditSessionViewModel extends VyfeViewModel {
    private String newDescription;
    private String newName;


    public EditSessionViewModel(String companyId) {
        sessionRepository = new SessionRepository(companyId);
    }

    public void init(String sessionId) {
        loadSession(sessionId);
    }

    @Override
    protected void onCleared() {
        sessionRepository.removeListeners();
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
