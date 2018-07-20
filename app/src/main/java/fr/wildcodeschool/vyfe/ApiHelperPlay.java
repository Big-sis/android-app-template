package fr.wildcodeschool.vyfe;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ApiHelperPlay {
    private static FirebaseDatabase mDatabase = SingletonFirebase.getInstance().getDatabase();
    private static String mAuthUserId = SingletonFirebase.getInstance().getUid();
    private static SingletonSessions mSingletonSessions = SingletonSessions.getInstance();
    private static String mIdTagSet;
    private static String mIdSession = mSingletonSessions.getIdSession();
    private static HashMap<String, Integer> mTagColorList;

    public static void getTags(final ArrayList<TagModel> mTagedList, final ArrayList<TagModel> mTagModels, final ApiHelperPlay.TagsResponse listener) {

        listener.onWait();
        final DatabaseReference sessionRef = mDatabase.getReference(mAuthUserId).child("sessions").child(mIdSession);
        sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mIdTagSet = dataSnapshot.child("idTagSet").getValue(String.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final DatabaseReference tagSessionRef = mDatabase.getReference(mAuthUserId).child("sessions").child(mIdSession).child("tags");
        tagSessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTagedList.clear();
                for (DataSnapshot tagSnapshot : dataSnapshot.getChildren()) {
                    TagModel tagModel = tagSnapshot.getValue(TagModel.class);
                    String tagName = tagSnapshot.child("tagName").getValue(String.class);
                    tagModel.setName(tagName);
                    mTagedList.add(tagModel);
                    final DatabaseReference tagRef = mDatabase.getReference(mAuthUserId).child("tags");
                    tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mTagModels.clear();
                            for (DataSnapshot tagsSnapshot : dataSnapshot.getChildren()) {
                                TagModel tagModel = tagsSnapshot.getValue(TagModel.class);
                                if (tagModel.getFkTagSet().equals(mIdTagSet)) {
                                    if (!mTagModels.contains(tagModel)) {
                                        mTagModels.add(tagModel);
                                    }
                                }
                                listener.onFinish();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            listener.onError(databaseError.getMessage());
                        }
                    });
                }
                listener.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });
    }

    interface TagsResponse {

        void onSuccess();

        void onError(String error);

        void onWait();

        void onFinish();
    }

    public static void getColors(final HashMap<String, Integer> mTagColorList, final ApiHelperPlay.ColorResponse listener) {
        final DatabaseReference tagRef = mDatabase.getReference(mAuthUserId).child("tags");
        tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot tagSnapshot : dataSnapshot.getChildren()) {
                    TagModel tagModel = tagSnapshot.getValue(TagModel.class);
                    if (tagModel.getFkTagSet().equals(mIdTagSet)) {
                        mTagColorList.put(tagModel.getName(), tagModel.getColor());
                    }
                }
                listener.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });
    }

    interface ColorResponse {

        void onSuccess();

        void onError(String error);

        void onWait();

        void onFinish();
    }
}
