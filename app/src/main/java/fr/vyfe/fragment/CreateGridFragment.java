package fr.vyfe.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.vyfe.R;
import fr.vyfe.adapter.TemplateRecyclerAdapter;
import fr.vyfe.helper.InternetConnexionHelper;
import fr.vyfe.model.TagSetModel;
import fr.vyfe.model.TemplateModel;
import fr.vyfe.viewModel.CreateGridViewModel;

public class CreateGridFragment extends Fragment implements View.OnClickListener {
    private CreateGridViewModel viewModel;
    private OnButtonClickedListener mCallback;
    private EditText gridTitleView;
    private RecyclerView recyclerTemplateList;
    private Button saveGridBtn;
    private TextView tvAddTag;
    private LinearLayout llimport;

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
        recyclerTemplateList = result.findViewById(R.id.recycler_view2);
        tvAddTag = result.findViewById(R.id.tv_add_tag2);

        result.findViewById(R.id.fab_add_moment2).setOnClickListener(this);
        result.findViewById(R.id.tv_add_tag2).setOnClickListener(this);


        llimport = result.findViewById(R.id.linear_layout_add);
        return result;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        gridTitleView.setText(viewModel.getTagSetName().getValue());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerTemplateList.setLayoutManager(layoutManager);


        TagSetModel tagSetModel = new TagSetModel();
        tagSetModel.setTagTemplates(viewModel.getTemplates().getValue());
        final TemplateRecyclerAdapter adapter = new TemplateRecyclerAdapter(tagSetModel.getTemplates(), "start", InternetConnexionHelper.isConnectedToInternet(getActivity()));
        recyclerTemplateList.setAdapter(adapter);

        viewModel.getTemplates().observe(getActivity(), new Observer<ArrayList<TemplateModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<TemplateModel> templates) {
                adapter.notifyDataSetChanged();
                if (templates != null && templates.size() != 0) {
                    tvAddTag.setText(R.string.edit_tags);
                    recyclerTemplateList.setVisibility(View.VISIBLE);
                    saveGridBtn.setVisibility(View.VISIBLE);
                } else {
                    tvAddTag.setText(R.string.create_tags);
                    recyclerTemplateList.setVisibility(View.GONE);
                    saveGridBtn.setVisibility(View.GONE);
                }
                if (templates.size() > 10) {
                    final Snackbar snackbar = Snackbar.make(getView(), R.string.info_max_tags, Snackbar.LENGTH_INDEFINITE).setDuration(9000).setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    View snackBarView = snackbar.getView();
                    TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setMinLines(2);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    snackbar.setDuration(7000);
                    snackbar.show();
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
                } else
                    Toast.makeText(getActivity(), R.string.grid_name_empty_warning, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        mCallback.onCreateGridFragmentButtonClicked(view);
    }

    private void createCallbackToParentActivity() {
        try {
            mCallback = (OnButtonClickedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + " must implement OnButtonClickedListener");
        }
    }

    public interface OnButtonClickedListener {
        void onCreateGridFragmentButtonClicked(View view);
    }
}
