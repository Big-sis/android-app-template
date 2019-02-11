package fr.vyfe.viewModel;


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

    public Task<Void> deleteSession() {
        onCleared();
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
        if (newName == null) newName = getSession().getValue().getName();
        if (newDescription == null) newDescription = getSession().getValue().getDescription();
        sessionModel.setName(this.newName);
        sessionModel.setDescription(this.newDescription);
        return sessionRepository.update(sessionModel);
    }

    public Task<Void> deleteLinkAppSession() {
        SessionModel sessionModel = session.getValue();
        sessionModel.setDeviceVideoLink(null);
        return sessionRepository.update(sessionModel);
    }

}
