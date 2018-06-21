package fr.wildcodeschool.vyfe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import petrov.kristiyan.colorpicker.ColorPicker;

public class AddGridActivity extends AppCompatActivity {
    int mfinalcolor;
    SingletonTags mSingletonTags = SingletonTags.getInstance();
    ArrayList<TagModel> mTagModelList = mSingletonTags.getmTagsList();
    final TagRecyclerAdapter mAdapter = new TagRecyclerAdapter(mTagModelList, "start");

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

        // Elements du recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AddGridActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerTagList.setLayoutManager(layoutManager);
        recyclerTagList.setHasFixedSize(true);
        recyclerTagList.setItemAnimator(new DefaultItemAnimator());

        if (mTagModelList != null) {
            recyclerTagList.setAdapter(mAdapter);

        }

        final Button btnAddEvenement = findViewById(R.id.btn_add);
        btnAddEvenement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valueName = etName.getText().toString();
                if (valueName.equals("") || mfinalcolor == 0) {
                    Toast.makeText(AddGridActivity.this, R.string.def_colot, Toast.LENGTH_SHORT).show();
                } else {
                    TagModel tagModel = new TagModel(mfinalcolor, valueName, null, null);
                    mTagModelList.add(tagModel);
                    mAdapter.notifyDataSetChanged();
                    mfinalcolor = 0;
                    etName.setText("");
                    ivColor.setBackgroundColor(Color.parseColor("#ffaaaaaa"));



                }

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnAddEvenement.getWindowToken(), 0);


            }
        });

        Button btnEnd = findViewById(R.id.btn_end);
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSingletonTags.setmTagsList(mTagModelList);


                Intent intent = new Intent(AddGridActivity.this, StartActivity.class);
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
                Toast.makeText(AddGridActivity.this, R.string.delete_tag, Toast.LENGTH_SHORT).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerTagList);


    }

    public void openColorChoose() {
        final ColorPicker colorPicker = new ColorPicker(this);
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


        colorPicker.setColors(colors)
                .setColumns(4)
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

    public void moveItem(int oldPos, int newPos) {

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

    public void deleteItem(final int position) {
        mTagModelList.remove(position);
        mAdapter.notifyItemRemoved(position);

    }
}
