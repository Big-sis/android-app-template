package fr.vyfe.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.vyfe.R;
import fr.vyfe.adapter.TimelineAdapter;
import fr.vyfe.helper.ScrollHelper;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.TimeModel;
import fr.vyfe.viewModel.RecordVideoViewModel;

public class TimelineRecordFragment extends Fragment {

    private RecordVideoViewModel viewModel;
    private RecyclerView recyclerView;
    private TimelineAdapter adapter;

    public static TimelineRecordFragment newInstance() {
        return new TimelineRecordFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(RecordVideoViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_timeline, container, false);
        recyclerView = view.findViewById(R.id.ll_main);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        viewModel.getTags().observe(getActivity(), new Observer<List<TagModel>>() {
            @Override
            public void onChanged(@Nullable List<TagModel> tags) {
                adapter = new TimelineAdapter(tags, null);
                recyclerView.setAdapter(adapter);

                /*
                for (TagModel tag: tags) {
                    String tagName = tag.getName();
                    TextView tvNameTimeline = new TextView(getContext());
                    tvNameTimeline.setTextColor(Color.WHITE);

                    boolean isFirstTitle = false;

                    if (!newTagList.containsKey(tagName)) {
                        ArrayList<Pair<Integer, Integer>> rTagList = new ArrayList<>();
                        newTagList.put(tagName, rTagList);
                        isFirstTitle = true;
                    }

                    //rapport pour la presentation
                    final int rapport = getResources().getInteger(R.integer.rapport_timeline);

                    //Ici on pourra changer les caracteristiques des tags pour la V2. Pour l'instant carac = constantes
                    final int durationTag = getResources().getInteger(R.integer.duration_tag) * rapport;
                    final int beforeTag = getResources().getInteger(R.integer.before_tag) * rapport;
                    int titleLength = getResources().getInteger(R.integer.title_length_timeline);

                    //init image Tag
                    ImageView iv = new ImageView(getActivity());
                    RelativeLayout.LayoutParams layoutParamsIv = new RelativeLayout.LayoutParams(
                            titleLength, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsIv.setMargins(0, convertToDp(8), 0, convertToDp(8));
                    iv.setLayoutParams(layoutParamsIv);
                    iv.setMinimumHeight(convertToDp(25));
                    iv.setBackgroundResource(viewModel.getSession().getTags().get(integer).getColor().getImage());

                    //init chrono
                    final int[] timeChrono = new int[1];
                    viewModel.getVideoTime().observe(getActivity(), new Observer<Long>() {
                        @Override
                        public void onChanged(@Nullable Long time) {
                            timeChrono[0] = (int) ((time / (1000 / rapport)));

                        }
                    });
                    int startTime = Math.max(0, timeChrono[0] - beforeTag);
                    int endTime = timeChrono[0] + durationTag;
                    iv.setMinimumWidth(endTime - startTime);

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(convertToDp(startTime), convertToDp(10), 0, convertToDp(10));
                    RelativeLayout timeline = mTimelineRowsMap.get(tagName);

                    timeline.addView(iv, layoutParams);
                    if (isFirstTitle) {
                        tvNameTimeline.setText(viewModel.getSession().getTags().get(integer).getName());
                        tvNameTimeline.setMinimumHeight(convertToDp(25));
                        RelativeLayout.LayoutParams layoutParamsTv = new RelativeLayout.LayoutParams(
                                convertToDp(titleLength), LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParamsTv.setMargins(convertToDp(15), convertToDp(10), convertToDp(8), convertToDp(10));
                        tvNameTimeline.setLayoutParams(layoutParamsTv);
                        timeline.addView(tvNameTimeline, layoutParamsTv);
                    }

                    //Pour envoit sur firebase
                    Pair<Integer, Integer> timePair = new Pair<>(startTime / rapport, endTime / rapport);
                    TimeModel time = new TimeModel();
                    time.setStart(startTime / rapport);
                    time.setEnd(endTime / rapport);
                    if (viewModel.getSession().getTags().get(integer).getTimes() == null) {
                        viewModel.getSession().getTags().get(integer).setTimes(new ArrayList<TimeModel>());
                    }
                    viewModel.getSession().getTags().get(integer).getTimes().add(time);

                    int count = viewModel.getSession().getTags().get(integer).getCount();
                    count++;
                    viewModel.getSession().getTags().get(integer).setCount(count);
                    viewModel.setCount(count);

                    //Scrool automatiquement suit l'ajout des tags
                    final HorizontalScrollView scrollView = view.findViewById(R.id.horizontal_scroll_view);
                    ScrollHelper.RigthScroll(scrollView);
                }
                */
            }
        });
    }

    private int convertToDp(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics());
    }


}
