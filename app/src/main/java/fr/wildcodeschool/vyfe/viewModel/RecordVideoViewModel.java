package fr.wildcodeschool.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;

import fr.wildcodeschool.vyfe.model.SessionModel;

public class RecordVideoViewModel extends ViewModel {

    private SessionModel session;

    public RecordVideoViewModel() {

    }

    public SessionModel getSession() {

        if (session == null) {
            session = new SessionModel();
        }
        return session;
    }
}
