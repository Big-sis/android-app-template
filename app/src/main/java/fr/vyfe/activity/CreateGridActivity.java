package fr.vyfe.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import fr.vyfe.R;
import fr.vyfe.fragment.CreateGridFragment;
import fr.vyfe.viewModel.CreateGridViewModel;
import fr.vyfe.viewModel.CreateGridViewModelFactory;

public class CreateGridActivity extends VyfeActivity {
    private CreateGridViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this, new CreateGridViewModelFactory(mAuth.getCurrentUser().getId(), mAuth.getCurrentUser().getCompany())).get(CreateGridViewModel.class);

        setContentView(R.layout.activity_prepare_session);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.edit_grid);
        setSupportActionBar(toolbar);

        replaceFragment(R.id.activity_create_grid, CreateGridFragment.newInstance());
    }
}

