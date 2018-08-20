package fr.wildcodeschool.vyfe;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApiHelperSpinner {
    private static final String authUserId = SingletonFirebase.getInstance().getUid();
    private static FirebaseDatabase mDatabase = SingletonFirebase.getInstance().getDatabase();
    private static String mNameGrid;
    private static ArrayList<String> mGridNames = new ArrayList<>();
    private static HashMap<String, String> hashMapTitleIdGrid = new HashMap<>();

    public static void getSpinner(final Context context, final ApiHelperSpinner.GridResponse listener) {

        //tagSetShared données pour mettre spinner
        DatabaseReference myRef = mDatabase.getReference(authUserId).child("tagSets");
        myRef.keepSynced(true);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.getChildrenCount() == 0) {
                    hashMapTitleIdGrid.put("0", context.getString(R.string.havent_grid));
                    listener.onSuccess(hashMapTitleIdGrid);
                } else {
                    hashMapTitleIdGrid.clear();
                    mGridNames.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mNameGrid = snapshot.child("name").getValue().toString();
                        if (mNameGrid != null && !mGridNames.contains(mNameGrid)) {
                            mGridNames.add(mNameGrid);
                            String idGrid = snapshot.getKey();
                            hashMapTitleIdGrid.put(idGrid, mNameGrid);
                            Log.d("Spinner", "onDataChange: ");
                        }
                    }
                    listener.onSuccess(hashMapTitleIdGrid);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });
    }

    public static ArrayList<TagModel> getTags(final Context context, int i) {
        final ArrayList<TagModel> mTagModelListAdd = new ArrayList<>();
        final String[] str = {context.getString(R.string.arrow)};
        final ArrayList<String> mNameTagSet = new ArrayList<>();
        final ArrayList<String> mIdTagSet = new ArrayList<>();
        String titleTagSet = "";


        mNameTagSet.clear();
        mNameTagSet.add(context.getString(R.string.import_grid_arrow) + str[0]);
        mIdTagSet.clear();
        mIdTagSet.add("0");
        for (Map.Entry<String, String> entry : hashMapTitleIdGrid.entrySet()) {
            mNameTagSet.add(entry.getValue());
            mIdTagSet.add(entry.getKey());
        }

        final String mIdGridImport = mIdTagSet.get(i);
        final String titlenameTagSetImport = mNameTagSet.get(i);

        if (mIdGridImport != null && mIdGridImport.equals("0")) {
            return null;
        }
        mTagModelListAdd.clear();
        //adapterNotifyDataChange(adapter, adapterImport);
        if (mIdGridImport != null && !mIdGridImport.equals(context.getString(R.string.import_grid_arrow) + str[0])) {

            DatabaseReference myRefTag = mDatabase.getReference(authUserId).child("tags");
            myRefTag.keepSynced(true);
            myRefTag.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() == 0) {
                        Toast.makeText(context, R.string.havent_tag,
                                Toast.LENGTH_SHORT).show();
                    }
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        if (snapshot.child("fkTagSet").getValue().toString() != null
                                && snapshot.child("fkTagSet").getValue().toString().equals(mIdGridImport)) {
                            String name = (String) snapshot.child("name").getValue();
                            String color = (snapshot.child("color").getValue().toString());
                            mTagModelListAdd.add(new TagModel(color, name, null, null));
                        }
                    }
                    //adapterImport.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            titleTagSet = titlenameTagSetImport;
        }
        return mTagModelListAdd;







    }


    interface GridResponse {

        void onSuccess(HashMap<String, String> hashMapTitleIdGrid);

        void onError(String error);

        void onWait(String wait);

        void onFinish(String finish);
    }
}

