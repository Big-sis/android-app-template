package fr.vyfe.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.vyfe.R;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.TemplateModel;
import fr.vyfe.viewModel.PlayVideoViewModel;

public class TimelinePlayFragment extends Fragment {
    private static final int WIDTH_THUMB = 15;

    private PlayVideoViewModel viewModel;
    private SeekBar mSeekBar;
    private LinearLayout containerLayout;
    private ArrayList<TextView> tvRowNameArray = new ArrayList<>();

    private RelativeLayout relativeLayout;

    public static TimelinePlayFragment newInstance() {
        return new TimelinePlayFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(PlayVideoViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        mSeekBar = view.findViewById(R.id.seek_bar_selected);
        containerLayout = view.findViewById(R.id.ll_timeline_container);

        relativeLayout = view.findViewById(R.id.time_lines_container);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        // Applique les paramètres à la seekBar
        RelativeLayout.LayoutParams seekBarParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        seekBarParams.setMargins(-WIDTH_THUMB, 0, 0, 0);

        mSeekBar.setLayoutParams(seekBarParams);

        viewModel.getVideoPosition().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer position) {
                mSeekBar.setProgress(position);
            }
        });


        viewModel.getTagSet().observe(getActivity(), new Observer<TagSetModel>() {
            @Override
            public void onChanged(@Nullable TagSetModel tagSetModel) {
                if (tagSetModel.getTemplates() != null) {
                    int titleLength = getResources().getInteger(R.integer.title_length_timeline);
                    for (TemplateModel template : tagSetModel.getTemplates()) {
                        RelativeLayout timelineRowView = new RelativeLayout(getContext());
                        timelineRowView.setTag(template.getId());
                        timelineRowView.setBackgroundResource(R.drawable.color_gradient_grey_nocolor);

                        TextView tvNameRow = new TextView(getContext());
                        tvNameRow.setTextColor(Color.WHITE);
                        tvNameRow.setText(template.getName());
                        tvNameRow.setMinimumHeight(convertToDp(15));
                        RelativeLayout.LayoutParams layoutParamsTv = new RelativeLayout.LayoutParams(
                                convertToDp(titleLength), LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParamsTv.setMargins(convertToDp(15), convertToDp(8), convertToDp(8), convertToDp(8));
                        tvNameRow.setLayoutParams(layoutParamsTv);
                        timelineRowView.addView(tvNameRow, layoutParamsTv);
                        tvRowNameArray.add(tvNameRow);


                        containerLayout.addView(timelineRowView, new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    }

                    viewModel.getTags().observe(getActivity(), new Observer<List<TagModel>>() {
                        @Override
                        public void onChanged(@Nullable List<TagModel> tags) {

                            for (TagModel tag : tags) {

                                int timelineWidth = containerLayout.getWidth();
                                int videoDurationSecond = viewModel.getSession().getValue().getDuration() / 1000;
                                ImageView iv = new ImageView(getContext());

                                iv.setBackgroundResource(tag.getColor().getImage());

                                int tagLength = Math.max(convertToDp(25), (tag.getEnd() - tag.getStart()) * timelineWidth / videoDurationSecond);
                                RelativeLayout.LayoutParams layoutParamsIv = new RelativeLayout.LayoutParams(
                                        tagLength, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParamsIv.setMargins(tag.getStart() * timelineWidth / videoDurationSecond, convertToDp(8), 0, convertToDp(8));
                                iv.setMinimumHeight(convertToDp(20));
                                iv.setLayoutParams(layoutParamsIv);

                                RelativeLayout timelineRow = containerLayout.findViewWithTag(tag.getTemplateId());
                                timelineRow.addView(iv);
                            }

                            for (TextView textView : tvRowNameArray) {
                                textView.bringToFront();
                            }

                        }
                    });

                    //Thumb adapter à la Timeline
                    ViewTreeObserver vto = mSeekBar.getViewTreeObserver();
                    vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        public boolean onPreDraw() {
                            Drawable thumb = getResources().getDrawable(R.drawable.thumb_blue);
                            int h = containerLayout.getMeasuredHeight();
                            Bitmap bmpOrg = ((BitmapDrawable) thumb).getBitmap();
                            Drawable newThumb = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bmpOrg, WIDTH_THUMB, h, true));
                            newThumb.setBounds(0, 0, newThumb.getIntrinsicWidth(), newThumb.getIntrinsicHeight());
                            mSeekBar.setThumb(newThumb);
                            mSeekBar.getViewTreeObserver().removeOnPreDrawListener(this);
                            mSeekBar.setMax(viewModel.getSession().getValue().getDuration());

                            viewModel.setTimelinesize(containerLayout.getMeasuredHeight());
                            return true;
                        }
                    });


                }
            }
        });


        viewModel.getSession().observe(getActivity(), new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel session) {
                if (session == null) return;
                mSeekBar.setMax(session.getDuration());
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                viewModel.setSeekPosition(i);
                viewModel.setVideoPosition(i);
                viewModel.isMoveSeek().setValue(false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                viewModel.pause();
                viewModel.isMoveSeek().setValue(true);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                viewModel.play();
                viewModel.isMoveSeek().setValue(true);

            }
        });
    }

    private int convertToDp(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getActivity().getResources().getDisplayMetrics());
    }
}
