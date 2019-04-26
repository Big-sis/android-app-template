package fr.vyfe.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import fr.vyfe.R;
import fr.vyfe.fragment.CreateGridFragment;
import fr.vyfe.fragment.CreateTemplatesFragment;
import fr.vyfe.viewModel.CreateGridViewModel;
import fr.vyfe.viewModel.CreateGridViewModelFactory;

public class CreateGridActivity extends VyfeActivity {
    private CreateGridViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new CreateGridViewModelFactory(mAuth.getCurrentUser().getId(), getDisplayName(), mAuth.getCurrentUser().getCompany())).get(CreateGridViewModel.class);

        setContentView(R.layout.activity_prepare_session);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.edit_grid);
        setSupportActionBar(toolbar);

        replaceFragment(R.id.create_grid_fragment_container, CreateGridFragment.newInstance());
        replaceFragment(R.id.create_template_fragment_container, CreateTemplatesFragment.newInstance());
    }

}

