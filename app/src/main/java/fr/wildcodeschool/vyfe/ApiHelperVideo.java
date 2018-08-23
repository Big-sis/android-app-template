package fr.wildcodeschool.vyfe;

import android.content.Context;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.widget.GridView;
import android.widget.Toast;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class ApiHelperVideo {
    private static FirebaseDatabase mDatabase = SingletonFirebase.getInstance().getDatabase();
    private static String authUserId = SingletonFirebase.getInstance().getUid();
    private static SingletonSessions mSingletonSessions = SingletonSessions.getInstance();
    private static ArrayList<SessionsModel> mSessionsModelList = mSingletonSessions.getmSessionsList();
    private static boolean Wait = true;


    public static void getVideo(final Context context, final GridView gridView, final ForecastResponse listener) {
        final String idAndroid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        final HashCode hashCode = Hashing.sha256().hashString(idAndroid, Charset.defaultCharset());

        final File[] sessionsCacheDirs = ContextCompat.getExternalCacheDirs(context);
        final String racineFile = String.valueOf(sessionsCacheDirs[0].getAbsoluteFile());
        final String[] numbersFiles = sessionsCacheDirs[0].list();

        final GridAdapter mGridAdapter = new GridAdapter(context, mSessionsModelList);

        DatabaseReference myRef = mDatabase.getReference(authUserId);
        Query query = myRef.child("sessions").orderByChild("idAndroid").equalTo(hashCode.toString());
        query.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSessionsModelList.clear();

                if (dataSnapshot.getChildrenCount() == 0) {
                    Toast.makeText(context, R.string.havent_video, Toast.LENGTH_LONG).show();
                }
                final long[] pendingLoadCount = {dataSnapshot.getChildrenCount()};

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    SessionsModel sessionsModel = snapshot.getValue(SessionsModel.class);

                    for (String numberFile : numbersFiles) {
                        String nameCache = racineFile +"/"+ numberFile;
                        if (sessionsModel.getVideoLink().equals(nameCache)) {
                            mSessionsModelList.add(sessionsModel);
                            //TODO: mettre un message a utilisateur plus dispo ou les faire apparaitre en plus clair
                        }
                    }

                }
                GridAdapter adapter = new GridAdapter(context, mSessionsModelList);
                gridView.setAdapter(adapter);

                pendingLoadCount[0] = pendingLoadCount[0] - 1;
                if (pendingLoadCount[0] == 0) {
                    listener.onFinish();
                }
                if (pendingLoadCount[0] != 0 && Wait) {
                    listener.onWait();
                    Wait = false;


                }
                listener.onSuccess(mSessionsModelList);
                mGridAdapter.notifyDataSetChanged();

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });


    }

    interface ForecastResponse {

        void onSuccess(ArrayList<SessionsModel> result);

        void onError(String error);

        void onWait();

        void onFinish();
    }

}
