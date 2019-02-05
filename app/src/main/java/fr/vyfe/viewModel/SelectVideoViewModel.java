package fr.vyfe.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;

import fr.vyfe.Constants;
import fr.vyfe.model.SessionModel;
import fr.vyfe.repository.BaseSingleValueEventListener;
import fr.vyfe.repository.SessionRepository;
import fr.vyfe.repository.TagSetRepository;
import fr.vyfe.model.CompanyModel;
import fr.vyfe.repository.CompanyRepository;


public class SelectVideoViewModel extends VyfeViewModel {

    private CompanyRepository companyRepository;
    private MutableLiveData<CompanyModel> company;

    public SelectVideoViewModel(String companyId, String userId) {
        companyRepository = new CompanyRepository(companyId);
        sessionRepository = new SessionRepository(companyId);
        tagSetRepository = new TagSetRepository(userId, companyId);
    }

    public void init(String sessionId) {
        this.sessionId = sessionId;
        loadCompany();
    }

    public LiveData<CompanyModel> getCompany() {
        if (company == null) {
            company = new MutableLiveData<>();
            loadCompany();
        }
        return company;
    }

    public void loadCompany() {
        if (company == null) {
            company = new MutableLiveData<>();
        }
        companyRepository.addChildListener("", true, new BaseSingleValueEventListener.CallbackInterface<CompanyModel>() {
            @Override
            public void onSuccess(CompanyModel result) {
                company.setValue(result);
            }

            @Override
            public void onError(Exception e) {
                company.setValue(null);
            }
        });
    }

    @Override
    protected void loadSession(final String id) {
        if (session == null)
            session = new MutableLiveData<>();


        sessionRepository.addChildListener(id, false, new BaseSingleValueEventListener.CallbackInterface<SessionModel>() {
            @Override
            public void onSuccess(SessionModel result) {

                session.postValue(result);
                if (tagSetRepository != null)
                    SelectVideoViewModel.super.loadSession(id);
            }

            @Override
            public void onError(Exception e) {
                session.setValue(null);
            }
        });
    }

    public Task<Void> setServerVideoLink(String url) {
        SessionModel sessionModel = session.getValue();
        sessionModel.setServerVideoLink(url);
        return sessionRepository.update(sessionModel);
    }
}
