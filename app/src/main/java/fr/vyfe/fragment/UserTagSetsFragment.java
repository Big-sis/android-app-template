package fr.vyfe.fragment;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;

import fr.vyfe.R;
import fr.vyfe.RecyclerTouchListener;
import fr.vyfe.adapter.TagSetsRecyclerAdapter;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.viewModel.CreateGridViewModel;

public class UserTagSetsFragment extends Fragment {
    private TagSetsRecyclerAdapter adapter;
    private Button selectButton;
    private CreateGridViewModel viewModel;
    private RecyclerView recyclerView;

    public static UserTagSetsFragment newInstance() {
        return new UserTagSetsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(CreateGridViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_user_tag_sets, container, false);
        return result;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        selectButton = view.findViewById(R.id.select_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        viewModel.getAllTagSets().observe(this, new Observer<ArrayList<TagSetModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<TagSetModel> tagSetModels) {
                populateRecyclerView(view, tagSetModels);
            }
        });

        onClickEvent(view);
    }

    private void populateRecyclerView(View view, ArrayList<TagSetModel> tagSetModels) {

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new TagSetsRecyclerAdapter(getContext(), tagSetModels);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(final View view, int position) {
                viewModel.setSelectedTagSet(viewModel.getAllTagSets().getValue().get(position));
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void onClickEvent(View view) {
        view.findViewById(R.id.show_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final HashMap<TagSetModel, Boolean> select = adapter.getSelectedTagSetIds();
                if (select.size() > 0) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.confirm_delete_tag_sets)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    for (TagSetModel tagSetModel : select.keySet()) {
                                        if (select.get(tagSetModel))
                                            viewModel.deleteTagSets(tagSetModel.getId());
                                        if (tagSetModel.getId().equals(viewModel.getSelectedTagSet().getValue().getId()))
                                            viewModel.getSelectedTagSet().setValue(new TagSetModel());
                                    }
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();

                }
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check the current text of Select Button
                if (selectButton.getText().toString().equals(getResources().getString(R.string.select_all))) {

                    //If Text is Select All then loop to all array List items and check all of them
                    for (int i = 0; i < viewModel.getAllTagSets().getValue().size(); i++)
                        adapter.checkCheckBox(viewModel.getAllTagSets().getValue().get(i), true);

                    selectButton.setText(getResources().getString(R.string.select_nothing));
                } else {
                    //If button text is Deselect All remove check from all items
                    adapter.removeSelection();
                    selectButton.setText(getResources().getString(R.string.select_all));
                }
            }
        });
    }
}
