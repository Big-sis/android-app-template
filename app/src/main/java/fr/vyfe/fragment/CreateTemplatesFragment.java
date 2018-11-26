package fr.vyfe.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import fr.vyfe.R;
import fr.vyfe.adapter.ColorSpinnerAdapter;
import fr.vyfe.adapter.TemplateRecyclerAdapter;
import fr.vyfe.helper.ColorHelper;
import fr.vyfe.helper.KeyboardHelper;
import fr.vyfe.model.ColorModel;
import fr.vyfe.model.TagModel;
import fr.vyfe.model.TemplateModel;
import fr.vyfe.viewModel.CreateGridViewModel;

public class CreateTemplatesFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener  {

    private CreateGridViewModel viewModel;
    ColorSpinnerAdapter colorSpinnerAdapter;
    private CreateTemplatesFragment.OnButtonClickedListener mCallback;
    Spinner colorSpinnerView;
    private static TemplateRecyclerAdapter mAdapter;
    private static ImageView ivColor;
    private static ColorModel tagColor;


    public static CreateTemplatesFragment newInstance() {
        return new CreateTemplatesFragment();
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
        mAdapter = new TemplateRecyclerAdapter(viewModel.getTemplates().getValue(), "create");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.create_tags_fragment, container, false);
        result.findViewById(R.id.end_btn).setOnClickListener(this);
        return result;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){

        final EditText tagNameView = view.findViewById(R.id.tag_name_edit);
        final RecyclerView recyclerTagList = view.findViewById(R.id.recycler_view);
        ivColor = view.findViewById(R.id.iv_color);

        colorSpinnerView = view.findViewById(R.id.colorSpinner);
        colorSpinnerView.setOnItemSelectedListener(this);
        colorSpinnerAdapter = new ColorSpinnerAdapter(getContext(), ColorHelper.getInstance().getColors());
        colorSpinnerView.setAdapter(colorSpinnerAdapter);
        randomSelectSpinnerColor();


        // Elements du recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerTagList.setLayoutManager(layoutManager);
        recyclerTagList.setHasFixedSize(true);
        recyclerTagList.setItemAnimator(new DefaultItemAnimator());
        recyclerTagList.setAdapter(mAdapter);

        final Button btnAddTag = view.findViewById(R.id.add_tag_btn);
        btnAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tagName = tagNameView.getText().toString();

                if (tagName.equals("")) {
                    Toast.makeText(getContext(), R.string.def_color, Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.addTemplate(tagColor, tagName);
                    tagNameView.setText("");

                    //Fermer clavier après avoir rentré un tag
                    KeyboardHelper.CloseKeyboard(getContext(), btnAddTag);
                    randomSelectSpinnerColor();
                }
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(getContext(), R.string.move, Toast.LENGTH_SHORT).show();
                moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                deleteItem(viewHolder.getAdapterPosition());
                Toast.makeText(getContext(), R.string.delete_tag, Toast.LENGTH_SHORT).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerTagList);

        viewModel.getTemplates().observe(getActivity(), new Observer<ArrayList<TemplateModel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<TemplateModel> templates) {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        ColorModel mfinalcolor = colorSpinnerAdapter.getItem(position);
        tagColor = colorSpinnerAdapter.getItem(position);
        ivColor.setBackgroundResource(mfinalcolor.getImage());
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        randomSelectSpinnerColor();
    }

    public void randomSelectSpinnerColor() {
        int random = (int) (Math.random() * (colorSpinnerAdapter.getCount() - 1));
        colorSpinnerView.setSelection(random);
    }

    public void moveItem(int oldPos, int newPos) {
        viewModel.moveTag(oldPos, newPos);
        mAdapter.notifyItemMoved(oldPos, newPos);
    }

    public void deleteItem(int position) {
        viewModel.getTemplates().getValue().remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onClick(View view) {
        mCallback.onCreateTagsFragmentButtonClicked(view);
    }

    private void createCallbackToParentActivity(){
        try {
            mCallback = (OnButtonClickedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString()+ " must implement OnButtonClickedListener");
        }
    }

    public interface OnButtonClickedListener {
        void onCreateTagsFragmentButtonClicked(View view);
    }
}
