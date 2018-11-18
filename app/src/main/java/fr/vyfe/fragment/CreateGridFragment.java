package fr.vyfe.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.vyfe.R;
import fr.vyfe.adapter.TagRecyclerAdapter;
import fr.vyfe.model.TagModel;
import fr.vyfe.viewModel.CreateGridViewModel;

public class CreateGridFragment extends Fragment implements View.OnClickListener {
    private CreateGridViewModel viewModel;
    private OnButtonClickedListener mCallback;
    private EditText gridTitleView;
    private RecyclerView recyclerTagList;
    private Button saveGridBtn;
    private TextView tvAddTag;


    public static CreateGridFragment newInstance() {
        return new CreateGridFragment();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.createCallbackToParentActivity();
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
        saveGridBtn = result.findViewById(R.id.save_grid_btn);
        recyclerTagList = result.findViewById(R.id.recycler_view2);
        tvAddTag = result.findViewById(R.id.tv_add_tag2);

        result.findViewById(R.id.fab_add_moment2).setOnClickListener(this);
        result.findViewById(R.id.tv_add_tag2).setOnClickListener(this);

        return result;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        gridTitleView.setText(viewModel.getTagSetName().getValue());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerTagList.setLayoutManager(layoutManager);
        final TagRecyclerAdapter adapter = new TagRecyclerAdapter(viewModel.getTags().getValue(), "start");
        recyclerTagList.setAdapter(adapter);

        viewModel.getTags().observe(getActivity(), new Observer<ArrayList<TagModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<TagModel> tagModels) {
                adapter.notifyDataSetChanged();
                if (tagModels != null && tagModels.size() != 0) {
                    tvAddTag.setText(R.string.edit_tags);
                    recyclerTagList.setVisibility(View.VISIBLE);
                    saveGridBtn.setVisibility(View.VISIBLE);
                }
                else {
                    tvAddTag.setText(R.string.create_tags);
                    recyclerTagList.setVisibility(View.GONE);
                    saveGridBtn.setVisibility(View.GONE);
                }
            }
        });

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
            }
        });

        //Actions management
        saveGridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gridTitleView.getText().toString().isEmpty()) {
                    viewModel.save();
                    Toast.makeText(getActivity(), R.string.save_grid_info, Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
                else
                    Toast.makeText(getActivity(), R.string.grid_name_empty_warning, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        mCallback.onCreateGridFragmentButtonClicked(view);
    }

    private void createCallbackToParentActivity(){
        try {
            mCallback = (OnButtonClickedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString()+ " must implement OnButtonClickedListener");
        }
    }

    public interface OnButtonClickedListener {
        void onCreateGridFragmentButtonClicked(View view);
    }
}
