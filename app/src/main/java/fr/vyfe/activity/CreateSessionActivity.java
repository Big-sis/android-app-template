package fr.vyfe.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import fr.vyfe.R;
import fr.vyfe.model.UserModel;
import fr.vyfe.viewModel.CreateSessionViewModel;
import fr.vyfe.viewModel.CreateSessionViewModelFactory;

public class CreateSessionActivity extends VyfeActivity {

    private CreateSessionViewModel viewModel;
    public boolean isMulti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserModel currentUser = mAuth.getCurrentUser();
        viewModel = ViewModelProviders.of(this, new CreateSessionViewModelFactory(currentUser.getId(), currentUser.getCompany())).get(CreateSessionViewModel.class);

        setContentView(R.layout.activity_create_session);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.start_session);

        isMulti = getIntent().getBooleanExtra("multiSession", false);
    }

}