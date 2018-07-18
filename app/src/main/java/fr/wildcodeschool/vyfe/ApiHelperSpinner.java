package fr.wildcodeschool.vyfe;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class ApiHelperSpinner {
    private static FirebaseDatabase mDatabase = SingletonFirebase.getInstance().getDatabase();
    private static final String authUserId = SingletonFirebase.getInstance().getUid();
    private static String mNameGrid;
    private static boolean Wait = true;


    public static void getSpinner(final Context context, final ApiHelperSpinner.GridResponse listener) {

        final String[] str = {context.getString(R.string.arrow)};
        //tagSetShared données pour mettre spinner
        DatabaseReference myRef = mDatabase.getReference(authUserId).child("tagSets");
        myRef.keepSynced(true);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String, String> hashMapTitleIdGrid = new HashMap<>();

                if (dataSnapshot.getChildrenCount() == 0) {
                    hashMapTitleIdGrid.put("0", context.getString(R.string.havent_grid));
                } else {
                    hashMapTitleIdGrid.clear();


                    final long[] pendingLoadCount = {dataSnapshot.getChildrenCount()};
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mNameGrid = (String) snapshot.child("name").getValue().toString();
                        String idGrid = (String) snapshot.getKey().toString();

                        hashMapTitleIdGrid.put(idGrid,mNameGrid);
                        Log.d("Spinner", "onDataChange: ");

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

        void onSuccess(HashMap<String, String>  hashMapTitleIdGrid);

        void onError(String error);

        void onWait(String wait);

        void onFinish(String finish);
    }
}
