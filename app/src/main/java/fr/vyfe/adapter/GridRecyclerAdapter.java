package fr.vyfe.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import java.util.ArrayList;

import fr.vyfe.R;
import fr.vyfe.model.TagSetModel;

public class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.ViewHolder> {

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView label;
            private CheckBox checkBox;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                label = (TextView) itemView.findViewById(R.id.grid_name);
                checkBox = (CheckBox) itemView.findViewById(R.id.select_grid_checkbox);
            }
        }

    private ArrayList<TagSetModel> arrayList;
    private Context context;
    private SparseBooleanArray mSelectedItemsIds;


    public GridRecyclerAdapter(Context context, ArrayList<TagSetModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public GridRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_all_grid, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int i) {
        holder.label.setText(arrayList.get(i).getName());
        holder.checkBox.setChecked(mSelectedItemsIds.get(i));

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCheckBox(i, !mSelectedItemsIds.get(i));
            }
        });

        holder.label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCheckBox(i, !mSelectedItemsIds.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    /**
     * Remove all checkbox Selection
     **/
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    /**
     * Check the Checkbox if not checked
     **/
    public void checkCheckBox(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, true);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    /**
     * Return the selected Checkbox IDs
     **/
    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }


}