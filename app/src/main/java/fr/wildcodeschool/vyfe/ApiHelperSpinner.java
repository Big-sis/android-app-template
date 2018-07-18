package fr.wildcodeschool.vyfe;

import android.content.Context;
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
    private static final ArrayList<String> nameTagSet = new ArrayList<>();
    private static String mNameGrid;
    private static final HashMap<String, String> hashMapTitleIdGrid = new HashMap<>();
    private static boolean Wait = true;


    public static void getSpinner(final Context context, final ApiHelperSpinner.GridResponse listener) {
        final ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(context,
                R.layout.simple_spinner, nameTagSet);
        final String[] str = {context.getString(R.string.arrow)};
        //tagSetShared données pour mettre spinner
        DatabaseReference myRef = mDatabase.getReference(authUserId).child("tagSets");
        myRef.keepSynced(true);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    nameTagSet.add(context.getString(R.string.havent_grid));
                } else {
                    nameTagSet.clear();

                    byte spbyte[] = new byte[0];
                    try {
                        spbyte = str[0].getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    try {
                        str[0] = new String(spbyte, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    nameTagSet.add(context.getString(R.string.import_grid_arrow) + str[0]);

                    final long[] pendingLoadCount = {dataSnapshot.getChildrenCount()};
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mNameGrid = (String) snapshot.child("name").getValue().toString();
                        String idGrid = (String) snapshot.getKey().toString();
                        boolean simpleGridName = true;

                        for (int i = 0; i < nameTagSet.size(); i++) {
                            if (mNameGrid.equals(nameTagSet.get(i))) {
                                simpleGridName = false;
                            }
                        }
                        if (simpleGridName) {
                            hashMapTitleIdGrid.put(mNameGrid, idGrid);
                            nameTagSet.add(mNameGrid);
                            simpleGridName = true;
                        }
                        pendingLoadCount[0] = pendingLoadCount[0] - 1;
                        if (pendingLoadCount[0] == 0) {
                            listener.onFinish("Fin");
                        }
                        if (pendingLoadCount[0] != 0 && Wait) {
                            listener.onWait("Attente");
                            Wait = false;
                        }
                    }
                    listener.onSuccess(nameTagSet);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });
    }


    interface GridResponse {

        void onSuccess(ArrayList<String> result);

        void onError(String error);

        void onWait(String wait);

        void onFinish(String finish);
    }
}
