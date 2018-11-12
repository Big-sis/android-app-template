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

        //TODO : BDD 2 : test ok
        final String authUserId = SingletonFirebase.getInstance().getUid();

        mDatabase = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL);
        DatabaseReference idTagSetRef = mDatabase.getReference("NomEntreprise").child("Users").child(authUserId).child("TagSets");
        idTagSetRef.keepSynced(true);
        String idTagSet = idTagSetRef.push().getKey();
        idTagSetRef.child(idTagSet).child("name").setValue(titleTagSet);

        setTags(mTagModelListAdd, idTagSet,context);

    }

    public static void setTags(ArrayList<TagModel> mTagModelListAdd, String idTagSet, Context context) {
        //TODO : BDD2 : tester ok

        final String authUserId = SingletonFirebase.getInstance().getUid();
        mDatabase = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL);

        for (int i = 0; i < mTagModelListAdd.size(); i++) {
            String colorTag = mTagModelListAdd.get(i).getColor();
            String nameTag = mTagModelListAdd.get(i).getTagName();
            //V2 : choisir le temps, necessaire ???
            String durationTag = String.valueOf(context.getResources().getInteger(R.integer.duration_tag));
            String beforeTag = String.valueOf(context.getResources().getInteger(R.integer.before_tag));

            DatabaseReference tagsRef = mDatabase.getReference("NomEntreprise").child("Users").child(authUserId).child("TagSets").child(idTagSet).child("Tags");
            tagsRef.keepSynced(true);
            String idTag = tagsRef.push().getKey();
            tagsRef.child(idTag).child("color").setValue(colorTag);
            tagsRef.child(idTag).child("name").setValue(nameTag);
            tagsRef.child(idTag).child("leftOffset").setValue(beforeTag);
            tagsRef.child(idTag).child("rigthOffset").setValue(durationTag);

        }
    }

}
