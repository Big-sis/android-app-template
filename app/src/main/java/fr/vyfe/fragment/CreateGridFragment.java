package fr.vyfe.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.vyfe.ApiHelperGrid;
import fr.vyfe.R;
import fr.vyfe.activity.CreateGridActivity;
import fr.vyfe.adapter.TagRecyclerAdapter;
import fr.vyfe.model.TagModel;
import fr.vyfe.viewModel.SingletonTags;

public class CreateGridFragment extends Fragment {
    private SharedPreferences mSharedPrefTagSet;
    private EditText mEtTagSet;
    private RecyclerView mRecyclerTagList;


    public static CreateGridFragment newInstance() {
        return new CreateGridFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_grid, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //TODO: revoir av leviewModel , enlever Singleton etc
        // => replaceFragment(R.id.scroll_timeline, _OldCreateGridActivity.newInstance());
        //Elements page
        mEtTagSet = getView().findViewById(R.id.et_grid_title2);
        final TextView tvAddTag = getView().findViewById(R.id.tv_add_tag2);
        ImageView fabAddMoment = getView().findViewById(R.id.fab_add_moment2);
        Button btnSaveGrid = getView().findViewById(R.id.btn_save_session);
         mRecyclerTagList = getView().findViewById(R.id.recycler_view2);

        //Loading data
        SingletonTags mSingletonTags = SingletonTags.getInstance();
        final ArrayList<TagModel> mTagModelListAdd = mSingletonTags.getmTagsListAdd();

        //Actions management
        onPressAddTags(fabAddMoment);
        onPressAddTags(tvAddTag);
        btnSaveGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String titleGrid = mEtTagSet.getText().toString();
                if(!titleGrid.isEmpty() && mTagModelListAdd!=null){
                    ApiHelperGrid.setGrid(titleGrid,mTagModelListAdd,getContext());
                   //TODO : attention si lenregistrement est en cours lannule : a revoir
                    Toast.makeText(getActivity(), "Grille enregistrée", Toast.LENGTH_LONG).show();
                    cleanSharedPref();
                    mTagModelListAdd.clear();
                    gridRecyclerView(mTagModelListAdd);
                }else {
                    Toast.makeText(getActivity(), "Vous devez renseigner un titre à votre grille et des tags", Toast.LENGTH_LONG).show();
                }


            }
        });

        //Display management
        createSharedPreferences();
        gridRecyclerView(mTagModelListAdd);
         if (mTagModelListAdd.size() != 0) {
        tvAddTag.setText(R.string.edit_tags);
        visibility(0,mRecyclerTagList,btnSaveGrid);
        }

    }
    public void onPressAddTags (View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  mSharedPrefTagSet.edit().putString("TAGSET", mEtTagSet.getText().toString()).apply();
                Intent intent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    intent = new Intent(getContext(), CreateGridActivity.class);
                }
                startActivity(intent);
            }
        });

    }

    public void createSharedPreferences () {

        //Creation sharedPreference
          mSharedPrefTagSet = getContext().getSharedPreferences("TAGSET", Context.MODE_PRIVATE);
        String tagSetShared = mSharedPrefTagSet.getString("TAGSET", "");
        if (!tagSetShared.isEmpty()) {
            mEtTagSet.setText(tagSetShared);
        }

    }

    public void gridRecyclerView(ArrayList<TagModel> mTagModelListAdd){
        mRecyclerTagList.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerTagList.setLayoutManager(layoutManager);
        final TagRecyclerAdapter adapter = new TagRecyclerAdapter(mTagModelListAdd, "start");
        mRecyclerTagList.setAdapter(adapter);
    }

    public void visibility(int visibility, RecyclerView recyclerView, Button btnSave){
        recyclerView.setVisibility(visibility);
        btnSave.setVisibility(visibility);

    }

    public void cleanSharedPref() {
        mSharedPrefTagSet.edit().putString("TAGSET", "").apply();
        mEtTagSet.setText("");
    }



}
