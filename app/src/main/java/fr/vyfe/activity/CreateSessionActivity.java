package fr.vyfe.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.model.UserModel;
import fr.vyfe.viewModel.CreateSessionViewModel;
import fr.vyfe.viewModel.CreateSessionViewModelFactory;


/**
 * This activity handles Session configuration before recording
 * Accept EXTRA "multiSession" set to true in case of a session with observers (Raspberry)
 * Accept EXTRA "restartSession" in case of a repeat session after record
 * TODO : Test use cases with EXTRAS
 */
public class CreateSessionActivity extends VyfeActivity {

    public boolean isMulti;
    private CreateSessionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserModel currentUser = mAuth.getCurrentUser();
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (currentUser.getLastName() != null && currentUser.getFirstname() != null)
            viewModel = ViewModelProviders.of(this, new CreateSessionViewModelFactory(currentUser.getId(), getDisplayName(), currentUser.getCompany(), androidId)).get(CreateSessionViewModel.class);

        if (getIntent().hasExtra(Constants.SESSIONTITLE_EXTRA) && getIntent().hasExtra(Constants.TAGSETID_EXTRA))
            viewModel.init((String) getIntent().getStringExtra(Constants.SESSIONTITLE_EXTRA), (String) getIntent().getStringExtra(Constants.TAGSETID_EXTRA));

        setContentView(R.layout.activity_create_session);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.start_session);

        isMulti = getIntent().getBooleanExtra("multiSession", false);
    }

}