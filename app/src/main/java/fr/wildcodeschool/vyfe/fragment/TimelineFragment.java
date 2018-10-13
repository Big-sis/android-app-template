package fr.wildcodeschool.vyfe.fragment;

import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import fr.wildcodeschool.vyfe.R;
import fr.wildcodeschool.vyfe.adapter.TimelineAdapter;
import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.model.TagModel;
import fr.wildcodeschool.vyfe.viewModel.PlayVideoViewModel;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class TimelineFragment extends Fragment {
    private static final int WIDTH_THUMB = 15;

    private RecyclerView recyclerView;
    private SeekBar mSeekBar;
    private PlayVideoViewModel viewModel;

    public static TimelineFragment newInstance() {
        return new TimelineFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(PlayVideoViewModel.class);

        viewModel.getVideoPosition().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer position) {
                mSeekBar.setProgress(position);
            }
        });

        viewModel.getSession().observe(getActivity(), new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel session) {
                if (session.getTags() != null) {
                    List<TagModel> taglist = new ArrayList<TagModel>(session.getTags().values());
                    recyclerView.setAdapter(new TimelineAdapter(taglist, session.getDuration()));
                }
                mSeekBar.setMax(session.getDuration());
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                viewModel.setVideoPosition(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                viewModel.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                viewModel.play();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        recyclerView = view.findViewById(R.id.ll_main_playvideo);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Applique les paramètres à la seekBar
        RelativeLayout.LayoutParams seekBarParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        seekBarParams.setMargins(-WIDTH_THUMB, 0, 0, 0);
        mSeekBar = view.findViewById(R.id.seek_bar_selected);
        mSeekBar.setLayoutParams(seekBarParams);
    }
}
