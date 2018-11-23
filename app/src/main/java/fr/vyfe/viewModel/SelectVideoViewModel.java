package fr.vyfe.viewModel;

import fr.vyfe.repository.SessionRepository;
import fr.vyfe.repository.TagSetRepository;


public class SelectVideoViewModel extends VyfeViewModel {

    public SelectVideoViewModel(String companyId, String userId) {
        sessionRepository = new SessionRepository(companyId);
        tagSetRepository = new TagSetRepository(userId, companyId);
    }

    public void init(String sessionId) {
        this.sessionId = sessionId;
    }
}
