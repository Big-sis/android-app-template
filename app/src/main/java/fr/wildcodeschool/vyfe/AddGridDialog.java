package fr.wildcodeschool.vyfe;


import android.content.Context;
import android.content.Intent;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AddGridDialog {

    private static int mfinalcolor = 0;
    private static SingletonTags mSingletonTags = SingletonTags.getInstance();
    private static ArrayList<TagModel> mTagModelList = mSingletonTags.getmTagsList();
    private static TagRecyclerAdapter mAdapter = new TagRecyclerAdapter(mTagModelList, "start");


    public static Dialog openCreateTags(final Context context) {


        final LayoutInflater inflater = LayoutInflater.from(context);
        final View subView = inflater.inflate(R.layout.activity_add_grid, null);

        final EditText etName = subView.findViewById(R.id.et_name);
        final ImageView ivColor = subView.findViewById(R.id.iv_color);
        final RecyclerView recyclerTagList = subView.findViewById(R.id.recycler_view);

        // Gestion couleurs
        Button btnChooseColor = subView.findViewById(R.id.btn_chosse_color);
        btnChooseColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //final ColorPicker colorPicker = new ColorPicker(StartActivity.this);
                final ArrayList<String> colors = new ArrayList<>();
                //TODO: remplacer couleur par celle de la charte graph
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
/*

                colorPicker.setColors(colors);
                colorPicker.setColumns(4);
                colorPicker.setRoundColorButton(true);
                colorPicker.setTitle(context.getString(R.string.choose_color));
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

*/
            }

        });

        final Button btnAleatory = subView.findViewById(R.id.btn_aleatory);
        btnAleatory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: associer la recherche aléatoire à l'arrayList color
                Random random = new Random();
                int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
                ivColor.setBackgroundColor(color);
                mfinalcolor = color;
            }
        });

        // Elements du recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
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
                if (valueName.equals("") || mfinalcolor == 0) {
                    Toast.makeText(context, R.string.def_colot, Toast.LENGTH_SHORT).show();
                } else {
                    TagModel tagModel = new TagModel(mfinalcolor, valueName, null, null);
                    mTagModelList.add(tagModel);
                    mAdapter.notifyDataSetChanged();
                    mfinalcolor = 0;
                    etName.setText("");
                    ivColor.setBackgroundColor(Color.parseColor("#ffaaaaaa"));

                    //Fermer clavier après avoir rentré un tag
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(btnAddEvenement.getWindowToken(), 0);

                }

            }
        });


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(context, R.string.move, Toast.LENGTH_SHORT).show();
                moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                deleteItem(viewHolder.getAdapterPosition());
                Toast.makeText(context, R.string.delete_tag, Toast.LENGTH_SHORT).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerTagList);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(subView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button btnEnd = subView.findViewById(R.id.btn_end);
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSingletonTags.setmTagsList(mTagModelList);
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
}
