package fr.wildcodeschool.vyfe;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CreateGridFragment extends Fragment {
    private SharedPreferences mSharedPrefTagSet;
    private EditText mEtTagSet;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_grid, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //TODO: implenter les elements de creation
        //mEtTagSet = getView().findViewById(R.id.et_grid_title2);
        final TextView tvAddTag = getView().findViewById(R.id.tv_add_tag2);
        ImageView fabAddMoment = getView().findViewById(R.id.fab_add_moment2);

        createSharedPreferences();
        onPressAddTags(fabAddMoment);
        onPressAddTags(tvAddTag);



        // if (mTagModelListAdd.size() != 0) {
        //tvAddTag.setText(R.string.edit_tags);
        //}
    }
    public void onPressAddTags (View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  mSharedPrefTagSet.edit().putString("TAGSET", mEtTagSet.getText().toString()).apply();
                Intent intent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    intent = new Intent(getContext(), AddGridActivity.class);
                }
                startActivity(intent);
            }
        });

    }

    public void createSharedPreferences () {
        //Creation sharedPreference
          mSharedPrefTagSet = this.getSharedPreferences("TAGSET", Context.MODE_PRIVATE);

        //tagSetShared des données
        String tagSetShared = mSharedPrefTagSet.getString("TAGSET", "");
        if (!tagSetShared.isEmpty()) {
            mEtTagSet.setText(tagSetShared);
        }

    }


}
