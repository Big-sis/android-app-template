package fr.vyfe.viewModel;

import android.arch.lifecycle.ViewModel;

import fr.vyfe.model.SessionModel;


public class SelectVideoViewModel extends ViewModel {
    private SessionModel session;


    public SelectVideoViewModel() {
    }

    public void init(SessionModel session) {
        this.session = session;
    }

    public SessionModel getSession() {
        return session;
    }
}
