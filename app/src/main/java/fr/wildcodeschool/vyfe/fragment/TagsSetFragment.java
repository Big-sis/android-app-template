package fr.wildcodeschool.vyfe.fragment;


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

import fr.wildcodeschool.vyfe.R;
import fr.wildcodeschool.vyfe.RecyclerTouchListener;
import fr.wildcodeschool.vyfe.adapter.TagRecyclerAdapter;
import fr.wildcodeschool.vyfe.viewModel.RecordVideoViewModel;

public class TagsSetFragment extends Fragment {

    private RecordVideoViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private TagRecyclerAdapter mAdapterTags;

    public static TagsSetFragment newInstance() {
        return new TagsSetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tags_set, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(getActivity()).get(RecordVideoViewModel.class);
        mRecyclerView = view.findViewById(R.id.re_tags);

        mAdapterTags = new TagRecyclerAdapter(mViewModel.getSession().getTags(), "record");
        RecyclerView.LayoutManager layoutManagerTags = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManagerTags);
        mRecyclerView.setAdapter(mAdapterTags);


        mViewModel.isRecording().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String step) {
                if (step.equals("recording")) {
                    mRecyclerView.setAlpha(1);
                } else {
                    mRecyclerView.setAlpha(0.5f);
                }
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                mViewModel.setTagPosition(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mViewModel.getCount().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                mAdapterTags.notifyDataSetChanged();
            }
        });


    }
}
