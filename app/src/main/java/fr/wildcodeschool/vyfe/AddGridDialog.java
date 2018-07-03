package fr.wildcodeschool.vyfe;


import android.content.Context;
import android.content.Intent;

import android.app.Dialog;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import petrov.kristiyan.colorpicker.ColorPicker;

public class AddGridDialog {

    private static int mfinalcolor = 0;
    private static SingletonTags mSingletonTags = SingletonTags.getInstance();
    private static ArrayList<TagModel> mTagModelList = mSingletonTags.getmTagsList();
    private static TagRecyclerAdapter mAdapter = new TagRecyclerAdapter(mTagModelList, "start");
    private static ArrayList<String> colors = new ArrayList<>();
    private static ImageView ivColor;
    private static ArrayList<String> nameDouble = new ArrayList<>();
    private static boolean repeatName = false;

    public static Dialog openCreateTags(final AppCompatActivity activity) {


        final LayoutInflater inflater = LayoutInflater.from(activity);
        final View subView = inflater.inflate(R.layout.activity_add_grid, null);
        final EditText etName = subView.findViewById(R.id.et_name);
        final RecyclerView recyclerTagList = subView.findViewById(R.id.recycler_view);
        ivColor = subView.findViewById(R.id.iv_color);

        // TODO mettre couleur à partir values
        colors.add("#F57A62");
        colors.add("#F56290");
        colors.add("#F562E5");
        colors.add("#F5EE62");
        colors.add("#62F5AB");
        colors.add("#69E3E7");
        colors.add("#3490E1");
        colors.add("#6977E7");
        colors.add("#3F51B5");
        colors.add("#343F6D");
        colors.add("#0D1725");
        colors.add("#d8d8d8");

        // Gestion couleurs
        Button btnChooseColor = subView.findViewById(R.id.btn_chosse_color);
        btnChooseColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ColorPicker colorPicker = new ColorPicker(activity);

                colorPicker.setColors(colors);
                colorPicker.setColumns(4);
                colorPicker.setRoundColorButton(true);
                colorPicker.setTitle(activity.getString(R.string.choose_color));
                colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        ImageView ivColor = subView.findViewById(R.id.iv_color);
                        ivColor.setBackgroundColor(color);
                        mfinalcolor = color;

                    }

                    @Override
                    public void onCancel() {

                    }
                });
                colorPicker.show();
            }

        });

        chooseColor();

        // Elements du recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        recyclerTagList.setLayoutManager(layoutManager);
        recyclerTagList.setHasFixedSize(true);
        recyclerTagList.setItemAnimator(new DefaultItemAnimator());

        if (mTagModelList != null) {
            recyclerTagList.setAdapter(mAdapter);

        }


        final ImageView btnAddEvenement = subView.findViewById(R.id.btn_add);
        btnAddEvenement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valueName = etName.getText().toString();
                for (int i = 0; i < nameDouble.size(); i++) {
                    if (valueName.equals(nameDouble.get(i))) {
                        repeatName = true;
                    }
                }

                if (repeatName) {
                    Toast.makeText(activity, R.string.double_name, Toast.LENGTH_SHORT).show();
                } else if ((valueName.equals("") || mfinalcolor == 0)) {
                    Toast.makeText(activity, R.string.def_colot, Toast.LENGTH_SHORT).show();
                } else {
                    TagModel tagModel = new TagModel(mfinalcolor, valueName, null, null);
                    mTagModelList.add(tagModel);
                    mAdapter.notifyDataSetChanged();
                    mfinalcolor = 0;
                    etName.setText("");
                    ivColor.setBackgroundColor(Color.parseColor("#ffaaaaaa"));

                    //Fermer clavier après avoir rentré un tag
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(btnAddEvenement.getWindowToken(), 0);
                    nameDouble.add(valueName);
                    chooseColor();
                }
            }
        });


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(activity, R.string.move, Toast.LENGTH_SHORT).show();
                moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                deleteItem(viewHolder.getAdapterPosition());
                Toast.makeText(activity, R.string.delete_tag, Toast.LENGTH_SHORT).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerTagList);


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(subView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button btnEnd = subView.findViewById(R.id.btn_end);
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSingletonTags.setmTagsList(mTagModelList);

                Intent intent = activity.getIntent();
                activity.finish();
                activity.startActivity(intent);

                alertDialog.cancel();

            }
        });


        return alertDialog;
    }


    public static void moveItem(int oldPos, int newPos) {

        if (oldPos < newPos) {
            for (int i = oldPos; i < newPos; i++) {
                Collections.swap(mTagModelList, i, i + 1);
            }
        } else {
            for (int i = oldPos; i > newPos; i--) {
                Collections.swap(mTagModelList, i, i - 1);
            }
        }
        mAdapter.notifyItemMoved(oldPos, newPos);
    }

    public static void deleteItem(final int position) {

        mAdapter.notifyItemRemoved(position);
        mSingletonTags.setmTagsList(mTagModelList);
    }

    public static void chooseColor() {
        Random random = new Random();
        int nbColor = random.nextInt(colors.size());
        String color = colors.get(nbColor);
        ivColor.setBackgroundColor(Color.parseColor(color));
        mfinalcolor = Color.parseColor(color);
    }
}
