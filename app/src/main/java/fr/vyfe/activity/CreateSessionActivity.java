package fr.vyfe.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.UserModel;
import fr.vyfe.viewModel.CreateSessionViewModel;
import fr.vyfe.viewModel.CreateSessionViewModelFactory;


/**
 * This activity handles Session configuration before recording
 *
 * Accept EXTRA "multiSession" set to true in case of a session with observers (Raspberry)
 * Accept EXTRA "restartSession" in case of a repeat session after record
 *
 * TODO : Test use cases with EXTRAS
 */
public class CreateSessionActivity extends VyfeActivity {

    private CreateSessionViewModel viewModel;
    public boolean isMulti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserModel currentUser = mAuth.getCurrentUser();
        viewModel = ViewModelProviders.of(this, new CreateSessionViewModelFactory(currentUser.getId(), currentUser.getCompany())).get(CreateSessionViewModel.class);
        if (getIntent().hasExtra(Constants.SESSIONMODEL_EXTRA))
            viewModel.init((SessionModel) getIntent().getParcelableExtra(Constants.SESSIONMODEL_EXTRA));

        setContentView(R.layout.activity_create_session);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.start_session);

        isMulti = getIntent().getBooleanExtra("multiSession", false);
    }

}