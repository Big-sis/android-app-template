package fr.vyfe.activity;

import android.os.Bundle;
import android.widget.Toolbar;

import fr.vyfe.R;
import fr.vyfe.fragment.AllGridFragment;
import fr.vyfe.viewModel.CreateGridViewModel;

public class GridActivity extends VyfeActivity {
    private CreateGridViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //viewModel = ViewModelProviders.of(this, new CreateGridViewModelFactory(mAuth.getCurrentUser().getId(), mAuth.getCurrentUser().getCompany())).get(CreateGridViewModel.class);

        setContentView(R.layout.activity_grid);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.edit_grid);
        setSupportActionBar(toolbar);

        replaceFragment(R.id.all_grid_fragment_container, AllGridFragment.newInstance());
       // replaceFragment(R.id.templates_fragment_container, TemplatesFragment.newInstance());
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }
}
