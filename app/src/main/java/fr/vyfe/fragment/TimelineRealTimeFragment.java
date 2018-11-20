package fr.vyfe.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import fr.vyfe.ColorNotFoundException;
import fr.vyfe.R;
import fr.vyfe.helper.ColorHelper;
import fr.vyfe.helper.ScrollHelper;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TimeModel;
import fr.vyfe.viewModel.RecordVideoViewModel;

public class TimelineRealTimeFragment extends Fragment {

    private RecordVideoViewModel viewModel;
    private HashMap<String, RelativeLayout> mTimelines = new HashMap<>();
    private HashMap<String, ArrayList<Pair<Integer, Integer>>> newTagList = new HashMap<>();

    public static TimelineRealTimeFragment newInstance() {
        return new TimelineRealTimeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline_real_time, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        //Init variable
        viewModel = ViewModelProviders.of(getActivity()).get(RecordVideoViewModel.class);

        //Ajout des differentes timelines au conteneur principal
        final LinearLayout llMain = view.findViewById(R.id.ll_main);
        for (TagModel tagModel : viewModel.getSession().getTags()) {
            String name = tagModel.getTagName();
            //Ajout d'un Linear pour un tag
            final RelativeLayout timeline = new RelativeLayout(getActivity());
            timeline.setBackgroundResource(R.drawable.color_gradient_grey_nocolor);
            llMain.addView(timeline);
            mTimelines.put(name, timeline);
        }

        viewModel.getTagPosition().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (viewModel.isRecording().getValue().equals("recording")) {

                    String nameTag = viewModel.getSession().getTags().get(integer).getTagName();
                    TextView tvNameTimeline = new TextView(getActivity());
                    tvNameTimeline.setTextColor(Color.WHITE);

                    boolean isFirstTitle = false;

                    if (!newTagList.containsKey(nameTag)) {
                        ArrayList<Pair<Integer, Integer>> rTagList = new ArrayList<>();
                        newTagList.put(nameTag, rTagList);
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
                    viewModel.getTimeChronometer().observe(getActivity(), new Observer<Long>() {
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
                    RelativeLayout timeline = mTimelines.get(nameTag);

                    timeline.addView(iv, layoutParams);
                    if (isFirstTitle) {
                        tvNameTimeline.setText(viewModel.getSession().getTags().get(integer).getTagName());
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
                    time.setStart(startTime/rapport);
                    time.setEnd(endTime/rapport);
                    if(viewModel.getSession().getTags().get(integer).getTimes()==null){
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

            }
        });
    }

    private int convertToDp(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics());
    }


}
