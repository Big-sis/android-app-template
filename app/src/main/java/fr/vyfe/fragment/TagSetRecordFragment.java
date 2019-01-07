package fr.vyfe.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.RecyclerTouchListener;
import fr.vyfe.adapter.TemplateRecyclerAdapter;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.viewModel.RecordVideoViewModel;

public class TagSetRecordFragment extends Fragment {

    private RecordVideoViewModel viewModel;
    private RecyclerView mRecyclerView;
    private TemplateRecyclerAdapter mTagAdpater;

    public static TagSetRecordFragment newInstance() {
        return new TagSetRecordFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(RecordVideoViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_set, container, false);
        mRecyclerView = view.findViewById(R.id.re_tags);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView.LayoutManager tagLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(tagLayoutManager);

        viewModel.getStep().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String step) {
                if (step.equals("recording")) {
                    mRecyclerView.setAlpha(1);

                    mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
                        @Override
                        public void onClick(final View view, int position) {

                            viewModel.addTag(position);
                            mTagAdpater.notifyDataSetChanged();
                        }

                        @Override
                        public void onLongClick(View view, int position) {
                            //TODO: ne fonctionne pas
                           // viewModel.addTag(position);
                        }
                    }));
                } else {
                    mRecyclerView.setAlpha(0.5f);
                }
            }
        });

        viewModel.getTagSet().observe(getActivity(), new Observer<TagSetModel>() {
            @Override
            public void onChanged(@Nullable final TagSetModel tagSet) {
                viewModel.getSession().observe(getActivity(), new Observer<SessionModel>() {
                    @Override
                    public void onChanged(@Nullable SessionModel sessionModel) {
                        if (tagSet != null) {
                            mTagAdpater = new TemplateRecyclerAdapter(tagSet.getTemplates(), "record");
                            mRecyclerView.setAdapter(mTagAdpater);
                        }
                    }
                });

            }
        });


    }
}
