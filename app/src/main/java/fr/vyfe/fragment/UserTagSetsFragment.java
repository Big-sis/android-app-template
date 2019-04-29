package fr.vyfe.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;

import fr.vyfe.R;
import fr.vyfe.adapter.CustomExpandableListAdapter;
import fr.vyfe.adapter.ExpandableListDataPump;
import fr.vyfe.adapter.TagSetsRecyclerAdapter;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.TemplateModel;
import fr.vyfe.viewModel.CreateGridViewModel;

public class UserTagSetsFragment extends Fragment {
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private ArrayList<TagSetModel> expandableListTitle;
    private HashMap<TagSetModel, ArrayList<TemplateModel>> expandableListDetail;
    private CreateGridViewModel viewModel;

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

        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        viewModel.getAllTagSets().observe(this, new Observer<ArrayList<TagSetModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<TagSetModel> tagSetModels) {

                expandableListDetail = ExpandableListDataPump.getData(tagSetModels);
                if (tagSetModels != null) {
                    expandableListTitle = new ArrayList<TagSetModel>(expandableListDetail.keySet());

                    expandableListAdapter = new CustomExpandableListAdapter(viewModel, getActivity(), expandableListTitle, expandableListDetail);
                    expandableListView.setAdapter(expandableListAdapter);
                }

            }
        });
    }


}
