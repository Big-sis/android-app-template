package fr.vyfe.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.model.ObserverModel;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.TemplateModel;
import fr.vyfe.viewModel.PlayVideoViewModel;

public class TimelinePlayFragment extends Fragment {
    private static final int WIDTH_THUMB = 15;
    private PlayVideoViewModel viewModel;
    private SeekBar mSeekBar;
    private LinearLayout teacherContainer;
    private ArrayList<TextView> tvRowNameArray = new ArrayList<>();
    private LinearLayout togetherContainer;
    private SharedPreferences sharedPreferences;

    public static TimelinePlayFragment newInstance() {
        return new TimelinePlayFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(PlayVideoViewModel.class);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        mSeekBar = view.findViewById(R.id.seek_bar_selected);
        teacherContainer = view.findViewById(R.id.ll_timeline_container_author);
        togetherContainer = view.findViewById(R.id.time_lines_container);
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


        viewModel.getSession().observe(getActivity(), new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel sessionModel) {

                //Create MainContainer Timeline
                if (sessionModel.getTagsSet().getTemplates() != null) {

                    //Param Author
                    String firstNameAuthor = sharedPreferences.getString(Constants.SHARED_PREF_USER_FIRSTNAME, "");
                    String lastNameAuthor = sharedPreferences.getString(Constants.SHARED_PREF_USER_LASTNAME, "");
                    if (!sharedPreferences.contains(Constants.SHARED_PREF_USER_FIRSTNAME))
                        firstNameAuthor = "";
                    if (!sharedPreferences.contains(Constants.SHARED_PREF_USER_LASTNAME)) lastNameAuthor = "";
                    String namesAuthor = firstNameAuthor + " " + lastNameAuthor;
                    ObserverModel observerModelAuthor = new ObserverModel();
                    observerModelAuthor.setName(namesAuthor);
                    observerModelAuthor.setId(viewModel.getSession().getValue().getAuthor());

                    //Creation teacher mainTimeline
                    createTimelineRow(observerModelAuthor, teacherContainer, sessionModel.getTagsSet());

                    //Creation Observers mainTimeline
                    ArrayList<ObserverModel> observers = viewModel.getSession().getValue().getObservers();
                    if (observers != null) for (ObserverModel observer : observers) {
                        LinearLayout layout = new LinearLayout(getContext());
                       String g =  observer.getId();
                        layout.setTag(observer.getId());
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        togetherContainer.addView(layout);
                        createTimelineRow(observer, layout, sessionModel.getTagsSet());
                    }

                    //Create Tags Timeline
                    ArrayList<TagModel> tags = sessionModel.getTags();
                    if (tags != null) {
                        for (TagModel tag : tags) {
                            // Create Teacher Tags
                            if (tag.getTaggerId().equals(viewModel.getSession().getValue().getAuthor())) {
                                ImageView ivTag = createIvTag(tag);
                                RelativeLayout timelineRow = teacherContainer.findViewWithTag(tag.getTemplateId());
                                timelineRow.addView(ivTag);

                            }
                            // Create Observers Tags
                            else {
                                ImageView ivTag = createIvTag(tag);
                                LinearLayout userContainer = togetherContainer.findViewWithTag(tag.getTaggerId());
                                if (userContainer != null) {
                                    RelativeLayout timelineRow = userContainer.findViewWithTag(tag.getTemplateId());
                                    timelineRow.addView(ivTag);
                                }
                            }
                        }

                        for (TextView textView : tvRowNameArray) {
                            textView.bringToFront();
                        }

                        //Thumb adapter à la Timeline
                        ViewTreeObserver vto = mSeekBar.getViewTreeObserver();
                        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            public boolean onPreDraw() {
                                Drawable thumb = getResources().getDrawable(R.drawable.thumb_blue);
                                int h = togetherContainer.getMeasuredHeight();
                                Bitmap bmpOrg = ((BitmapDrawable) thumb).getBitmap();
                                Drawable newThumb = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bmpOrg, WIDTH_THUMB, h, true));
                                newThumb.setBounds(0, 0, newThumb.getIntrinsicWidth(), newThumb.getIntrinsicHeight());
                                mSeekBar.setThumb(newThumb);
                                mSeekBar.getViewTreeObserver().removeOnPreDrawListener(this);
                                mSeekBar.setMax(viewModel.getSession().getValue().getDuration());
                                viewModel.setTimelinesize(togetherContainer.getMeasuredHeight());
                                return true;
                            }
                        });
                    }
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
                viewModel.isMoveSeek().setValue(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                viewModel.isMoveSeek().setValue(true);
            }
        });
    }

    private int convertToDp(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getActivity().getResources().getDisplayMetrics());
    }

    public LinearLayout createTimelineRow(ObserverModel observerModel, LinearLayout containerLayout, TagSetModel tagSetModel) {
        final TextView TvNameTagguer = new TextView(getContext());
        TvNameTagguer.setText(observerModel.getName());
        TvNameTagguer.setTextColor(Color.WHITE);
        TvNameTagguer.setMinimumHeight(convertToDp(15));
        RelativeLayout.LayoutParams layoutParamsTvTeacher = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsTvTeacher.setMargins(convertToDp(15), convertToDp(8), convertToDp(8), convertToDp(8));

        containerLayout.addView(TvNameTagguer, layoutParamsTvTeacher);

        TvNameTagguer.bringToFront();
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
        return containerLayout;
    }

    public ImageView createIvTag(TagModel tag) {
        int timelineWidth = teacherContainer.getWidth();
        int videoDurationSecond = viewModel.getSession().getValue().getDuration() / 1000;
        ImageView iv = new ImageView(getContext());
        iv.setBackgroundResource(tag.getColor().getImage());

        int tagLength = Math.max(convertToDp(25), (tag.getEnd() - tag.getStart()) * timelineWidth / videoDurationSecond);
        RelativeLayout.LayoutParams layoutParamsIv = new RelativeLayout.LayoutParams(
                tagLength, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsIv.setMargins(tag.getStart() * timelineWidth / videoDurationSecond, convertToDp(8), 0, convertToDp(8));
        iv.setMinimumHeight(convertToDp(20));
        iv.setLayoutParams(layoutParamsIv);
        return iv;
    }
}
