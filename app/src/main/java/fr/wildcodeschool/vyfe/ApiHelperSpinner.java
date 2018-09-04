package fr.wildcodeschool.vyfe;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

    private static FirebaseDatabase mDatabase = SingletonFirebase.getInstance().getDatabase();
    private static String mNameGrid;
    private static ArrayList<String> mGridNames = new ArrayList<>();
    private static HashMap<String, String> hashMapTitleIdGrid = new HashMap<>();

    private static ArrayList<TagModel> mTagModelListAdd = new ArrayList<>();

    public static void getSpinner(final Context context, final ApiHelperSpinner.GridResponse listener) {
        String authUserId = SingletonFirebase.getInstance().getUid();
        hashMapTitleIdGrid.clear();
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

    interface GridResponse {

        void onSuccess(HashMap<String, String> hashMapTitleIdGrid);

        void onError(String error);

        void onWait(String wait);

        void onFinish(String finish);
    }

    //TODO: Liste chargement ok mais retourne une liste vide : a corriger
    public static void getTag(final Context context, final RecyclerView recyclerView, final String mIdGridImport, final ApiHelperSpinner.TagsResponse listener){
        String authUserId = SingletonFirebase.getInstance().getUid();
        DatabaseReference myRefTag = mDatabase.getReference(authUserId).child("tags");
        myRefTag.keepSynced(true);
        myRefTag.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTagModelListAdd.clear();
                final TagRecyclerAdapter adapter = new TagRecyclerAdapter(mTagModelListAdd, "start");
                recyclerView.setAdapter(adapter);
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
                listener.onSuccess(mTagModelListAdd);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }

        });



    }
    interface TagsResponse {

        void onSuccess(ArrayList<TagModel> tagModelArrayList);
        void onError(String error);

        //TODO: creer une methode pour laffichage du nom dans spinner

    }
}

