package fr.vyfe.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import fr.vyfe.R;
import fr.vyfe.helper.ColorHelper;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TimeModel;
import fr.vyfe.viewModel.PlayVideoViewModel;

public class TimelineFragment extends Fragment {
    private static final int WIDTH_THUMB = 15;

    private SeekBar mSeekBar;
    private PlayVideoViewModel viewModel;
    private int mSizeTitle = 300;
    private int mWidth;

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
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        mWidth = display.getWidth();

        viewModel = ViewModelProviders.of(getActivity()).get(PlayVideoViewModel.class);

        viewModel.getVideoPosition().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer position) {
                mSeekBar.setProgress(position);
            }
        });

        if (viewModel.getSession().getTags() != null) {
            ArrayList<TagModel> tags = new ArrayList<TagModel>(viewModel.getSession().getTags());
        }
        mSeekBar.setMax(viewModel.getSession().getDuration());

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Applique les paramètres à la seekBar
        RelativeLayout.LayoutParams seekBarParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        seekBarParams.setMargins(-WIDTH_THUMB, 0, 0, 0);
        mSeekBar = view.findViewById(R.id.seek_bar_selected);
        mSeekBar.setLayoutParams(seekBarParams);
        viewModel = ViewModelProviders.of(getActivity()).get(PlayVideoViewModel.class);

        final LinearLayout mLlMain = view.findViewById(R.id.ll_main_playvideo);

        for (TagModel tag : viewModel.getSession().getTags()) {
            String tagName = tag.getTagName();

            //Creation de chaque etage de tags sur la timeline
            final RelativeLayout timelineRow = new RelativeLayout(getActivity());
            mLlMain.addView(timelineRow);

            TextView tvNameTimeline = new TextView(getActivity());
            tvNameTimeline.setText(tagName);
            tvNameTimeline.setTextColor(Color.WHITE);

            //Creation des noms pour chaque timeline
            RelativeLayout.LayoutParams layoutParamsTv = new RelativeLayout.LayoutParams(
                    convertToDp(mSizeTitle), LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsTv.setMargins(convertToDp(15), convertToDp(8), 0, convertToDp(8));
            tvNameTimeline.setTextColor(Color.WHITE);
            tvNameTimeline.setMinimumHeight(convertToDp(25));

            if (tag.getTimes() != null) {

                // Créé une image par utilisation du tag en cour
                for (final TimeModel tagTime : tag.getTimes()) {
                    //TODO: affichage ne fonctionne pas
                    double start = convertIntoTimelineViewRef(tagTime.getStart(), mWidth, viewModel.getSession());
                    double end = convertIntoTimelineViewRef(tagTime.getEnd(), mWidth, viewModel.getSession());

                    final ImageView tagImageView = new ImageView(getActivity());
                    RelativeLayout.LayoutParams layoutParamsIv = new RelativeLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutParamsIv.setMargins((int) Math.floor(start), convertToDp(8), 0, convertToDp(8));

                    tagImageView.setMinimumHeight(convertToDp(25));
                    tagImageView.setMinimumWidth(Math.max(convertToDp(50), (int) (end - start)));

                    tagImageView.setBackgroundResource(ColorHelper.getInstance().findColorById(tag.getColor().getId()).getImage());

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    timelineRow.setLayoutParams(layoutParams);
                    timelineRow.setBackgroundColor(getActivity().getResources().getColor(R.color.colorCharcoalGrey));
                    timelineRow.addView(tagImageView, layoutParamsIv);

                }
                timelineRow.addView(tvNameTimeline);
            }
        }

    }

    private double convertIntoTimelineViewRef(int value, int timelineWidth, SessionModel sessionModel) {
        int micro = value * getActivity().getResources().getInteger(R.integer.second_to_micro);
        double ratio = micro / sessionModel.getDuration();
        double convertValue = (ratio * timelineWidth) / getActivity().getResources().getInteger(R.integer.micro_to_milli);

        return convertValue;
    }

    private int convertToDp(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getActivity().getResources().getDisplayMetrics());
    }
}
