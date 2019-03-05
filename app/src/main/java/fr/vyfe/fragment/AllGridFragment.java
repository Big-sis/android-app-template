package fr.vyfe.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import fr.vyfe.R;
import fr.vyfe.adapter.GridRecyclerAdapter;
import fr.vyfe.viewModel.CreateGridViewModel;

public class AllGridFragment extends Fragment {
    private ArrayList<String> arrayList;
    private GridRecyclerAdapter adapter;
    private Button selectButton;
    private CreateGridViewModel viewModel;
    public static AllGridFragment newInstance() {
        return new AllGridFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(CreateGridViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Instancie la vue et les composant gaphique
        View result = inflater.inflate(R.layout.fragment_all_grid, container, false);
       // mEtVideoTitle = result.findViewById(R.id.et_video_title2);
        return result;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        populateRecyclerView(view);
        onClickEvent(view);
    }

    private void populateRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        arrayList = new ArrayList<>();
        for (int i = 1; i <= 50; i++)
            arrayList.add("RecyclerView Items " + i);//Adding items to recycler view

        adapter = new GridRecyclerAdapter(getContext(), arrayList);
        recyclerView.setAdapter(adapter);
    }

    private void onClickEvent(View view) {
        view.findViewById(R.id.show_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray selectedRows = adapter.getSelectedIds();
                if (selectedRows.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < selectedRows.size(); i++) {
                        if (selectedRows.valueAt(i)) {
                            String selectedRowLabel = arrayList.get(selectedRows.keyAt(i));
                            stringBuilder.append(selectedRowLabel + "\n");
                        }
                    }
                    Toast.makeText(getActivity(), "Selected Rows\n" + stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        view.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray selectedRows = adapter.getSelectedIds();
                if (selectedRows.size() > 0) {
                    for (int i = (selectedRows.size() - 1); i >= 0; i--) {
                        if (selectedRows.valueAt(i)) {
                            arrayList.remove(selectedRows.keyAt(i));
                        }
                    }
                    adapter.removeSelection();
                }
            }
        });
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check the current text of Select Button
                if (selectButton.getText().toString().equals(getResources().getString(R.string.select_all))) {

                    //If Text is Select All then loop to all array List items and check all of them
                    for (int i = 0; i < arrayList.size(); i++)
                        adapter.checkCheckBox(i, true);

                    //After checking all items change button text
                    selectButton.setText(getResources().getString(R.string.deselect_all));
                } else {
                    //If button text is Deselect All remove check from all items
                    adapter.removeSelection();

                    //After checking all items change button text
                    selectButton.setText(getResources().getString(R.string.select_all));
                }
            }
        });
}
