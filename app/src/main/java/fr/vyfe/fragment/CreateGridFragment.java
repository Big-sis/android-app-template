package fr.vyfe.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import fr.vyfe.Constants;
import fr.vyfe.R;
import fr.vyfe.helper.OpenInfoHelper;
import fr.vyfe.viewModel.CreateGridViewModel;

public class CreateGridFragment extends Fragment implements View.OnClickListener {
    private CreateGridViewModel viewModel;
    private OnButtonClickedListener mCallback;
    private EditText gridTitleView;
    private ConstraintLayout containerInfo;

    public static CreateGridFragment newInstance() {
        return new CreateGridFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(CreateGridViewModel.class);
        viewModel.init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_create_grid, container, false);
        gridTitleView = result.findViewById(R.id.grid_title_edit);
        containerInfo = result.findViewById(R.id.info);


        return result;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        OpenInfoHelper.setOnClick(Constants.INFO_CREATE_TAGSET,containerInfo, getContext());

        gridTitleView.setText(viewModel.getTagSetName().getValue());

        gridTitleView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setTagSetName(s.toString());
                viewModel.setIsEmptyTitleTagSet(false);
            }
        });

        viewModel.isEmptyTitleGrid().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(getActivity(), R.string.grid_name_empty_warning, Toast.LENGTH_LONG).show();
                    gridTitleView.setBackgroundResource(R.drawable.style_input_error);
                } else gridTitleView.setBackgroundResource(R.drawable.style_input);
            }
        });
    }

    @Override
    public void onClick(View view) {
        mCallback.onCreateGridFragmentButtonClicked(view);
    }


    public interface OnButtonClickedListener {
        void onCreateGridFragmentButtonClicked(View view);
    }
}
