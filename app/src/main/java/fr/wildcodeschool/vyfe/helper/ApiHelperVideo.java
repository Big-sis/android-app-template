package fr.wildcodeschool.vyfe.helper;

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

import fr.wildcodeschool.vyfe.adapter.VideoGridAdapter;
import fr.wildcodeschool.vyfe.R;
import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.viewModel.SingletonFirebase;
import fr.wildcodeschool.vyfe.viewModel.SingletonSessions;

import static android.os.Environment.DIRECTORY_MOVIES;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class ApiHelperVideo {
    private static FirebaseDatabase mDatabase = SingletonFirebase.getInstance().getDatabase();
    private static SingletonSessions mSingletonSessions = SingletonSessions.getInstance();
    private static ArrayList<SessionModel> mSessionsModelList = mSingletonSessions.getmSessionsList();
    private static boolean Wait = true;


    public static void getVideo(final Context context, final GridView gridView, final ForecastResponse listener) {

        // Recup vidéo sur mémoire en dure
         File externalStorage= getExternalStoragePublicDirectory(DIRECTORY_MOVIES+"/"+"Vyfe");
        final String racineExternalStorage= String.valueOf(externalStorage.getAbsoluteFile());
        final String [] filesExternalStorage = externalStorage.list();

        String authUserId = SingletonFirebase.getInstance().getUid();
        final String idAndroid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        final HashCode hashCode = Hashing.sha256().hashString(idAndroid, Charset.defaultCharset());

        //recup vidéo mémoire cache
        final File[] sessionsCacheDirs = ContextCompat.getExternalCacheDirs(context);
        final String racineFile = String.valueOf(sessionsCacheDirs[0].getAbsoluteFile());
        final String[] numbersFiles = sessionsCacheDirs[0].list();

        final VideoGridAdapter mVideoGridAdapter = new VideoGridAdapter(context, mSessionsModelList);

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

                //ancienne version: garde video memoire cache : celle ci disparaitra
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    SessionModel sessionsModel = snapshot.getValue(SessionModel.class);

                    for (String numberFile : numbersFiles) {
                        String nameCache = racineFile +"/"+ numberFile;
                        assert sessionsModel != null;
                        if (sessionsModel.getVideoLink().equals(nameCache)) {
                            mSessionsModelList.add(sessionsModel);
                            //TODO: mettre un message a utilisateur plus dispo ou les faire apparaitre en plus clair
                        }
                    }

                }

                //pour la nouvelle
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    SessionModel sessionsModel = snapshot.getValue(SessionModel.class);

                    for (String nameFileExternalStorage : filesExternalStorage) {
                        String nameCache = racineExternalStorage +"/"+ nameFileExternalStorage;
                        assert sessionsModel != null;
                        if (sessionsModel.getVideoLink().equals(nameCache)) {
                            mSessionsModelList.add(sessionsModel);
                            //TODO: mettre un message a utilisateur plus dispo ou les faire apparaitre en plus clair
                        }
                    }

                }

                //TODO: si le fichier n'est pas trouvé (effacer les données sur firebase)

                VideoGridAdapter adapter = new VideoGridAdapter(context, mSessionsModelList);
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
                mVideoGridAdapter.notifyDataSetChanged();

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });


    }

    public interface ForecastResponse {

        void onSuccess(ArrayList<SessionModel> result);

        void onError(String error);

        void onWait();

        void onFinish();
    }

}