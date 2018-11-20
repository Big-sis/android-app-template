package fr.vyfe.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import fr.vyfe.R;
import fr.vyfe.fragment.CreateGridFragment;
import fr.vyfe.fragment.CreateTagsFragment;
import fr.vyfe.viewModel.CreateGridViewModel;
import fr.vyfe.viewModel.CreateGridViewModelFactory;

public class CreateGridActivity extends VyfeActivity implements CreateGridFragment.OnButtonClickedListener, CreateTagsFragment.OnButtonClickedListener {
    private CreateGridViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this, new CreateGridViewModelFactory(mAuth.getCurrentUser().getId(), mAuth.getCurrentUser().getCompany())).get(CreateGridViewModel.class);

        setContentView(R.layout.activity_prepare_session);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.edit_grid);
        setSupportActionBar(toolbar);

        replaceFragment(R.id.create_grid_fragment_container, CreateGridFragment.newInstance());
    }

    @Override
    public void onCreateGridFragmentButtonClicked(View view) {
        replaceFragment(R.id.create_grid_fragment_container, CreateTagsFragment.newInstance());
    }

    @Override
    public void onCreateTagsFragmentButtonClicked(View view) {
        replaceFragment(R.id.create_grid_fragment_container, CreateGridFragment.newInstance());
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.create_grid_fragment_container).getClass() == CreateTagsFragment.class)
            replaceFragment(R.id.create_grid_fragment_container, CreateGridFragment.newInstance());
        else
            super.onBackPressed();
    }
}

