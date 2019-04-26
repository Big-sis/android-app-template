package fr.vyfe.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fr.vyfe.R;
import fr.vyfe.fragment.TemplatesFragment;
import fr.vyfe.fragment.UserTagSetsFragment;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.viewModel.CreateGridViewModel;
import fr.vyfe.viewModel.CreateGridViewModelFactory;

public class TagSetsActivity extends VyfeActivity {
    private CreateGridViewModel viewModel;
    private LinearLayout containCreateTagSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new CreateGridViewModelFactory(mAuth.getCurrentUser().getId(), getDisplayName(), mAuth.getCurrentUser().getCompany())).get(CreateGridViewModel.class);

        setContentView(R.layout.activity_tag_sets);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.your_tagSets);

        replaceFragment(R.id.all_grid_fragment_container, UserTagSetsFragment.newInstance());
        replaceFragment(R.id.templates_fragment_container, TemplatesFragment.newInstance());
        containCreateTagSet = findViewById(R.id.container_create_grid);
        containCreateTagSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TagSetsActivity.this, CreateGridActivity.class));
            }
        });

        final TextView numberTagSet = findViewById(R.id.number_tag_set_tv);
        viewModel.getAllTagSets().observe(this, new Observer<ArrayList<TagSetModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<TagSetModel> tagSetModels) {
                if (tagSetModels.size() == 0 || tagSetModels == null)
                    numberTagSet.setText(R.string.havent_tag_sets);
                else if (tagSetModels.size() == 1) {
                    numberTagSet.setText(getString(R.string.you_have) + " " + String.valueOf(tagSetModels.size()) + " " + getString(R.string.tag_set));
                } else
                    numberTagSet.setText(getString(R.string.you_have) + " " + String.valueOf(tagSetModels.size()) + " " + getString(R.string.tag_sets));
            }
        });

    }


}
