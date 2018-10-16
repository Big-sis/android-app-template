package fr.wildcodeschool.vyfe;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import fr.wildcodeschool.vyfe.model.TagModel;
import fr.wildcodeschool.vyfe.viewModel.SingletonFirebase;

public class ApiHelperGrid {
    private static FirebaseDatabase mDatabase;


    public static void setGrid(String titleTagSet, ArrayList<TagModel> mTagModelListAdd, Context context) {


        final String authUserId = SingletonFirebase.getInstance().getUid();
        mDatabase = SingletonFirebase.getInstance().getDatabase();
        DatabaseReference idTagSetRef = mDatabase.getReference(authUserId).child("tagSets");
        idTagSetRef.keepSynced(true);
        String idTagSet = idTagSetRef.push().getKey();
        idTagSetRef.child(idTagSet).child("name").setValue(titleTagSet);


        setTags(mTagModelListAdd, idTagSet,context);


    }

    public static void setTags(ArrayList<TagModel> mTagModelListAdd, String idTagSet, Context context) {
        final String authUserId = SingletonFirebase.getInstance().getUid();
        mDatabase = SingletonFirebase.getInstance().getDatabase();
        // a mettre dans create grid mTagModelListAdd = mSingletonTags.getmTagsListAdd();

        for (int i = 0; i < mTagModelListAdd.size(); i++) {

            String colorTag = mTagModelListAdd.get(i).getColor();
            String nameTag = mTagModelListAdd.get(i).getName();
            //V2 : choisir le temps, necessaire ???
            String durationTag = String.valueOf(context.getResources().getInteger(R.integer.duration_tag));
            String beforeTag = String.valueOf(context.getResources().getInteger(R.integer.before_tag));


            DatabaseReference tagsRef = mDatabase.getReference(authUserId).child("tags");
            tagsRef.keepSynced(true);
            String idTag = tagsRef.push().getKey();
            tagsRef.child(idTag).child("color").setValue(colorTag);
            tagsRef.child(idTag).child("name").setValue(nameTag);
            tagsRef.child(idTag).child("leftOffset").setValue(beforeTag);
            tagsRef.child(idTag).child("rigthOffset").setValue(durationTag);
            tagsRef.child(idTag).child("fkTagSet").setValue(idTagSet);

        }
    }

}
