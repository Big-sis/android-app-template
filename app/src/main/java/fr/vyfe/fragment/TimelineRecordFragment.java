package fr.vyfe.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fr.vyfe.R;
import fr.vyfe.model.SessionModel;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.TemplateModel;
import fr.vyfe.viewModel.RecordVideoViewModel;

public class TimelineRecordFragment extends Fragment {

    private RecordVideoViewModel viewModel;
    private LinearLayout containerLayout;
    private ArrayList<TextView> tvRowNameArray = new ArrayList<>();
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
        containerLayout = view.findViewById(R.id.ll_main);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        viewModel.getTagsSetSession().observe(getActivity(), new Observer<TagSetModel>() {
            @Override
            public void onChanged(@Nullable TagSetModel tagSetModel) {
                //Create timeline
                if (tagSetModel.getTemplates() != null) {
                    int titleLength = getResources().getInteger(R.integer.title_length_timeline);
                    for (TemplateModel template : tagSetModel.getTemplates()) {
                        RelativeLayout timelineRowView = new RelativeLayout(getContext());
                        timelineRowView.setTag(template.getId());
                        timelineRowView.setBackgroundResource(R.drawable.color_gradient_grey_nocolor);

                        TextView tvNameRow = new TextView(getContext());
                        tvNameRow.setText(template.getName());
                        tvNameRow.setMinimumHeight(convertToDp(20));
                        tvNameRow.setTextColor(Color.WHITE);
                        RelativeLayout.LayoutParams layoutParamsTv = new RelativeLayout.LayoutParams(
                                convertToDp(titleLength), LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParamsTv.setMargins(convertToDp(15), convertToDp(8), convertToDp(8), convertToDp(8));
                        tvNameRow.setLayoutParams(layoutParamsTv);
                        timelineRowView.addView(tvNameRow, layoutParamsTv);
                        tvRowNameArray.add(tvNameRow);
                        containerLayout.addView(timelineRowView, new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    }
                }

            }
        });

        viewModel.getSession().observe(getActivity(), new Observer<SessionModel>() {
            @Override
            public void onChanged(@Nullable SessionModel sessionModel) {

                //Create tags
                for (TagModel tag : sessionModel.getTags()) {
                    if(tag.getEnd()>0) {

                        int titleLength = getResources().getInteger(R.integer.title_length_timeline);
                        ImageView iv = new ImageView(getContext());
                        RelativeLayout.LayoutParams layoutParamsIv = new RelativeLayout.LayoutParams(
                                titleLength, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParamsIv.setMargins(0, convertToDp(8), 0, convertToDp(8));
                        iv.setLayoutParams(layoutParamsIv);
                        iv.setMinimumHeight(convertToDp(20));
                        iv.setBackgroundResource(tag.getColor().getImage());
                        iv.setMinimumWidth(convertToDp((Math.max(convertToDp(25), tag.getEnd() - tag.getStart()))));

                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(convertToDp(tag.getStart()), convertToDp(10), 0, convertToDp(10));
                        ((RelativeLayout) containerLayout.findViewWithTag(tag.getTemplateId())).addView(iv, layoutParams);
                    }

                }
                for (TextView textView : tvRowNameArray) {
                    textView.bringToFront();
                }
            }
        });

    }

    private int convertToDp(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics());
    }


}
