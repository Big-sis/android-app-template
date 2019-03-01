package fr.vyfe.fragment;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import fr.vyfe.R;
import fr.vyfe.adapter.ObserverRecyclerAdapter;
import fr.vyfe.helper.AuthHelper;
import fr.vyfe.model.ObserverModel;
import fr.vyfe.viewModel.RecordVideoViewModel;

public class CooperationFragment extends Fragment {
    private RecordVideoViewModel viewModel;
    private Switch liveRecordingSwitch;
    private RecyclerView mRecyclerViewObservers;
    private ObserverRecyclerAdapter mObserverAdapter;
    private TextView tvNumber;
    private AuthHelper mAuth;
    private WifiManager wifiManager;

    public static CooperationFragment newInstance() {
        return new CooperationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(RecordVideoViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cooperation, container, false);
        mRecyclerViewObservers = view.findViewById(R.id.rv_observer);
        liveRecordingSwitch = view.findViewById(R.id.switch_live_conexion);
        tvNumber = view.findViewById(R.id.tv_observer);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView.LayoutManager tagLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewObservers.setLayoutManager(tagLayoutManager);
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        liveRecordingSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConnectingToInternet()) {
                    if (liveRecordingSwitch.isChecked())
                        viewModel.isTagsActive();
                    else {
                        viewModel.isTagsInactive();
                        if (viewModel.getSession().getValue().getDuration() <= 0) {
                            viewModel.deleteObservers();
                        }
                    }
                    viewModel.activeCooperation();
                } else {
                    liveRecordingSwitch.setChecked(false);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.connecting_internet)
                            .setPositiveButton(R.string.start_wifi, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    wifiManager.setWifiEnabled(true);
                                }
                            })
                            .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
            }
        });

        viewModel.getStep().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String step) {
                if (step.equals(RecordVideoViewModel.STEP_RECODRING)) {
                    liveRecordingSwitch.setEnabled(false);
                }
            }
        });

        viewModel.getObserversSession().observe(this, new Observer<ArrayList<ObserverModel>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<ObserverModel> idsObservers) {
                ArrayList<String> ids = new ArrayList<String>();
                String observers;
                if (idsObservers != null) {
                    for (ObserverModel id : idsObservers) {
                        ids.add(id.getNameObserver());
                    }
                    observers = getString(R.string.participants) + String.valueOf(idsObservers.size());
                    viewModel.getSession().getValue().setObservers(idsObservers);

                } else {
                    observers = getString(R.string.participants) + String.valueOf(0);
                }
                mObserverAdapter = new ObserverRecyclerAdapter(ids);
                mRecyclerViewObservers.setAdapter(mObserverAdapter);
                tvNumber.setText(observers);
            }
        });
    }

    private boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        return true;
    }
}
