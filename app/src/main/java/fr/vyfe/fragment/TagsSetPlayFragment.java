package fr.vyfe.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.vyfe.R;
import fr.vyfe.adapter.TemplateRecyclerAdapter;
import fr.vyfe.helper.InternetConnexionHelper;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.viewModel.PlayVideoViewModel;

public class TagsSetPlayFragment extends Fragment {

    private PlayVideoViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private TemplateRecyclerAdapter mAdapterTags;

    public static TagsSetPlayFragment newInstance() {
        return new TagsSetPlayFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_set, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(getActivity()).get(PlayVideoViewModel.class);
        mRecyclerView = view.findViewById(R.id.re_tags);

        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManagerTags);

        mViewModel.getSession().observe(getActivity(), new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel sessionModel) {

            }
        });
        mViewModel.getTagSet().observe(getActivity(), new Observer<TagSetModel>() {
            @Override
            public void onChanged(@Nullable TagSetModel tagSet) {
                if (tagSet != null) {
                    mAdapterTags = new TemplateRecyclerAdapter(tagSet.getTemplates(), "count");
                    mRecyclerView.setAdapter(mAdapterTags);
                }
            }
        });
    }
}
