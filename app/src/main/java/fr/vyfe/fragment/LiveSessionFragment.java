package fr.vyfe.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import fr.vyfe.R;
import fr.vyfe.viewModel.RecordVideoViewModel;

public class LiveSessionFragment extends Fragment {
    private RecordVideoViewModel viewModel;
    private Switch liveRecordingSwitch;

    public static LiveSessionFragment newInstance() {
        return new LiveSessionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(RecordVideoViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_session, container, false);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        liveRecordingSwitch = view.findViewById(R.id.switch_live_conexion);
        liveRecordingSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (liveRecordingSwitch.isChecked())
                    viewModel.isTagsActive();
                else viewModel.isTagsInactive();
                viewModel.addActiveTags();
            }
        });
    }


}
