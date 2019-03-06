package fr.vyfe.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import fr.vyfe.R;
import fr.vyfe.model.TagSetModel;

public class TagSetsRecyclerAdapter extends RecyclerView.Adapter<TagSetsRecyclerAdapter.ViewHolder> {

    private ArrayList<TagSetModel> arrayList;
    private Context context;
    private HashMap<TagSetModel, Boolean> mSelectedTagSetIds;

    public TagSetsRecyclerAdapter(Context context, ArrayList<TagSetModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        getmSelectedTagSetIds();
    }

    @Override
    public TagSetsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tag_set_checkbox, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.label.setText(arrayList.get(position).getName());
        holder.checkBox.setChecked(mSelectedTagSetIds.get(arrayList.get(position)));

        if (holder.checkBox.isChecked()) {
            holder.label.setTextSize(20);
            holder.container.setBackgroundResource(R.drawable.color_gradient_faded_orange);
        } else {
            holder.label.setTextSize(17);
            holder.container.setBackgroundResource(R.drawable.color_gradient_grey_dark);
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkCheckBox(arrayList.get(position), !mSelectedTagSetIds.get(arrayList.get(position)));

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    /**
     * Remove all checkbox Selection
     **/
    public void removeSelection() {
        getmSelectedTagSetIds();
        notifyDataSetChanged();
    }

    /**
     * Check the Checkbox if not checked
     **/

    public void checkCheckBox(TagSetModel tagSetModel, boolean value) {
        if (value)
            mSelectedTagSetIds.put(tagSetModel, true);
        else {
            mSelectedTagSetIds.remove(tagSetModel);
            mSelectedTagSetIds.put(tagSetModel, false);
        }
        notifyDataSetChanged();
    }

    /**
     * Return the selected Checkbox IDs
     **/

    public HashMap<TagSetModel, Boolean> getSelectedTagSetIds() {
        return mSelectedTagSetIds;
    }

    public HashMap<TagSetModel, Boolean> getmSelectedTagSetIds() {
        mSelectedTagSetIds = new HashMap<>();
        for (TagSetModel tagSetModel : arrayList) {
            mSelectedTagSetIds.put(tagSetModel, false);
        }
        return mSelectedTagSetIds;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView label;
        private CheckBox checkBox;
        private LinearLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.grid_name);
            checkBox = (CheckBox) itemView.findViewById(R.id.select_grid_checkbox);
            container = itemView.findViewById(R.id.container);
        }
    }
}