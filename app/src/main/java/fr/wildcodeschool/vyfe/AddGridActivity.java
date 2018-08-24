package fr.wildcodeschool.vyfe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
/**
 * This activity create Tags (name + color)
 */
public class AddGridActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private static int mfinalcolor = 0;


    private static SingletonTags mSingletonTags = SingletonTags.getInstance();
    private static ArrayList<TagModel> mTagModelListAdd = mSingletonTags.getmTagsListAdd();
    private static TagRecyclerAdapter mAdapter = new TagRecyclerAdapter(mTagModelListAdd, "start");

    private static ArrayList<String> colors = new ArrayList<>();
    private static ImageView ivColor;
    private static ArrayList<String> nameDouble = new ArrayList<>();
    private static boolean repeatName = false;
    private static int color[] = {R.drawable.icons8_tri_d_croissant_96, R.drawable.color_gradient_blue_dark, R.drawable.color_gradient_blue_light, R.drawable.color_gradient_faded_orange, R.drawable.color_gradient_green, R.drawable.color_gradient_grey, R.drawable.color_gradient_rosy};
    private static String[] nameDrawable = {"", "color_gradient_blue_dark", "color_gradient_blue_light", "color_gradient_faded_orange", "color_gradient_green", "color_gradient_grey", "color_gradient_rosy"};
    private static String nameColorTag;
    private String[] colorName = {"Choisir une couleur", "Violet", "Bleu", "Orange", "Vert", "Gris", "Rose"};

    public static void chooseColor() {
        int chooserandom = 1 + (int) (Math.random() * (color.length - 1));
        nameColorTag = nameDrawable[chooserandom];
        try {
            ivColor.setBackgroundResource(ColorHelper.convertColor(nameColorTag));
        } catch (ColorNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grid);

        final Spinner spinner = (Spinner) findViewById(R.id.simpleSpinner);
        spinner.setOnItemSelectedListener(this);

        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), color, colorName, nameDrawable);
        spinner.setAdapter(customAdapter);


        final EditText etName = findViewById(R.id.et_name);
        final RecyclerView recyclerTagList = findViewById(R.id.recycler_view);
        ivColor = findViewById(R.id.iv_color);
        chooseColor();


        // Elements du recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AddGridActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerTagList.setLayoutManager(layoutManager);
        recyclerTagList.setHasFixedSize(true);
        recyclerTagList.setItemAnimator(new DefaultItemAnimator());

        if (mTagModelListAdd != null) {
            recyclerTagList.setAdapter(mAdapter);

        }

        final Button btnAddEvenement = findViewById(R.id.btn_add);
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
                    Toast.makeText(AddGridActivity.this, R.string.double_name, Toast.LENGTH_SHORT).show();
                    repeatName = false;
                } else if ((valueName.equals(""))) {
                    Toast.makeText(AddGridActivity.this, R.string.def_colot, Toast.LENGTH_SHORT).show();
                } else {
                    TagModel tagModel = new TagModel(nameColorTag, valueName, null, null);
                    mTagModelListAdd.add(tagModel);
                    mAdapter.notifyDataSetChanged();
                    mfinalcolor = 0;
                    etName.setText("");

                    //Fermer clavier après avoir rentré un tag
                    InputMethodManager imm = (InputMethodManager) AddGridActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(btnAddEvenement.getWindowToken(), 0);
                    nameDouble.add(valueName);
                    spinner.setSelection(0);
                    chooseColor();
                }
            }
        });

        Button btnEnd = findViewById(R.id.btn_end);
        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSingletonTags.setmTagsListAdd(mTagModelListAdd);
                colors.clear();

                Intent intent = new Intent(AddGridActivity.this, StartActivity.class);
                intent.putExtra("fromAdd", "fromAdd");
                startActivity(intent);
                finish();
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

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        if (position != 0) {
            mfinalcolor = color[position];
            nameColorTag = nameDrawable[position];
            ivColor.setBackgroundResource(mfinalcolor);

            Toast.makeText(getApplicationContext(), colorName[position], Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void moveItem(int oldPos, int newPos) {

        if (oldPos < newPos) {
            for (int i = oldPos; i < newPos; i++) {
                Collections.swap(mTagModelListAdd, i, i + 1);
            }
        } else {
            for (int i = oldPos; i > newPos; i--) {
                Collections.swap(mTagModelListAdd, i, i - 1);
            }
        }
        mAdapter.notifyItemMoved(oldPos, newPos);
    }

    public void deleteItem(final int position) {
        mTagModelListAdd.remove(position);
        mAdapter.notifyItemRemoved(position);
        nameDouble.remove(position);

    }

}