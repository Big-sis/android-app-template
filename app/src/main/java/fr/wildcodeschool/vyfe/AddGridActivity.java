package fr.wildcodeschool.vyfe;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import petrov.kristiyan.colorpicker.ColorPicker;

public class AddGridActivity extends AppCompatActivity {
    int mfinalcolor;
    static boolean mAddEvent = false;
    ArrayList<TagModel> mTagModelList = new ArrayList<>();
    final TagRecyclerAdapter madapter = new TagRecyclerAdapter(mTagModelList, "start");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grid);
        final EditText etName = findViewById(R.id.et_name);
        final ImageView ivColor = findViewById(R.id.iv_color);
        final RecyclerView recyclerTagList = findViewById(R.id.recycler_view);


        // Gestion couleurs
        Button btnChooseColor = findViewById(R.id.btn_chosse_color);
        btnChooseColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorChoose();
            }
        });

        final Button btnAleatory = findViewById(R.id.btn_aleatory);
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



        if(StartActivity.mChangeTag){
            mTagModelList = getIntent().getExtras().getParcelableArrayList("change");

        }
        // Elements du recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AddGridActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerTagList.setLayoutManager(layoutManager);
        recyclerTagList.setHasFixedSize(true);
        recyclerTagList.setItemAnimator(new DefaultItemAnimator());
        recyclerTagList.setAdapter(madapter);

        Button btnAddEvenement = findViewById(R.id.btn_add);
        btnAddEvenement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valueName = etName.getText().toString();
                if (valueName.equals("") || mfinalcolor == 0) {
                    Toast.makeText(AddGridActivity.this, R.string.def_colot, Toast.LENGTH_SHORT).show();
                } else {
                    mAddEvent = true;
                    TagModel tagModel = new TagModel(mfinalcolor, valueName);
                    mTagModelList.add(tagModel);
                    madapter.notifyDataSetChanged();
                    mfinalcolor = 0;
                    etName.setText("");
                    ivColor.setBackgroundColor(Color.parseColor("#ffaaaaaa"));
                }
            }
        });

        Button btnEnd = findViewById(R.id.btn_end);
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddGridActivity.this, StartActivity.class);
                intent.putParcelableArrayListExtra("list", mTagModelList);
                startActivity(intent);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Toast.makeText(AddGridActivity.this, R.string.move, Toast.LENGTH_SHORT).show();
                moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                deleteItem(viewHolder.getAdapterPosition());
                Toast.makeText(AddGridActivity.this, R.string.delete, Toast.LENGTH_SHORT).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerTagList);


    }

    public void openColorChoose() {
        final ColorPicker colorPicker = new ColorPicker(this);
        final ArrayList<String> colors = new ArrayList<>();
        //TODO: remplacer couleur par celle de la charte graph
        colors.add(("#bd000d"));
        colors.add(("#5a00c5"));
        colors.add(("#1e8900"));
        colors.add(("#b7bb00"));
        colors.add(("#b664ba"));

        colors.add(("#f91734"));
        colors.add(("#932bf9"));
        colors.add(("#5bba25"));
        colors.add(("#eded42"));
        colors.add(("#ea94ed"));

        colors.add(("#ff5f5e"));
        colors.add(("#ca62ff"));
        colors.add(("#90ed59"));
        colors.add(("#ffff77"));
        colors.add(("#ffc6ff"));


        colorPicker.setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)
                .setTitle(getString(R.string.choose_color))
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        ImageView ivColor = findViewById(R.id.iv_color);
                        ivColor.setBackgroundColor(color);
                        mfinalcolor = color;

                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();


    }

    void moveItem(int oldPos, int newPos) {

        if (oldPos < newPos) {
            for (int i = oldPos; i < newPos; i++) {
                Collections.swap(mTagModelList, i, i + 1);
            }
        } else {
            for (int i = oldPos; i > newPos; i--) {
                Collections.swap(mTagModelList, i, i - 1);
            }
        }
        madapter.notifyItemMoved(oldPos, newPos);
    }

    void deleteItem(final int position) {
        mTagModelList.remove(position);
        madapter.notifyItemRemoved(position);

    }

}
