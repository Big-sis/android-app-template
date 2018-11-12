package fr.wildcodeschool.vyfe.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import fr.wildcodeschool.vyfe.Constants;
import fr.wildcodeschool.vyfe.model.SessionModel;
import fr.wildcodeschool.vyfe.model.TagModel;
import fr.wildcodeschool.vyfe.viewModel.SingletonFirebase;

//TODO: trouver un meilleur nom
public class NewSessionPost {

    public static SessionModel sessionMapper(SessionModel sessionModel) {
         FirebaseDatabase mDatabase;
         String mAuthUserId = SingletonFirebase.getInstance().getUid();
        ArrayList<TagModel> mTagModels = sessionModel.getTags();


        if (mTagModels != null) {
            mDatabase = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_VERSION_URL);
            DatabaseReference sessionRef2 = mDatabase.getReference("NomEntreprise").child("Sessions");
            sessionRef2.keepSynced(true);
            String mIdSession = sessionRef2.push().getKey();
            sessionModel.setIdSession(mIdSession);

            sessionRef2.child(mIdSession).child("author").setValue(mAuthUserId);
            sessionRef2.child(mIdSession).child("name").setValue(sessionModel.getName());
            sessionRef2.child(mIdSession).child("idTagSet").setValue(sessionModel.getIdTagSet());
            sessionRef2.child(mIdSession).child("pathApp").setValue(sessionModel.getDeviceVideoLink());
            sessionRef2.child(mIdSession).child("idAndroid").setValue(sessionModel.getIdAndroid());
            sessionRef2.child(mIdSession).child("date").setValue(sessionModel.getDate());

            ArrayList<TagModel> tagModelList = new ArrayList<>();
            for (TagModel entry : mTagModels) {
                TagModel tagModel = new TagModel();

                String tagKey = sessionRef2.child(mIdSession).child("Tags").push().getKey();
                sessionRef2.child(mIdSession).child("Tags").child(tagKey).child("name").setValue(entry.getTagName());
                sessionRef2.child(mIdSession).child("Tags").child(tagKey).child("color").setValue(entry.getColor());
                sessionRef2.child(mIdSession).child("Tags").child(tagKey).child("Times").child(mAuthUserId).setValue(entry.getTimes());

                tagModel.setTimes(entry.getTimes());
                tagModel.setColor(entry.getColor());
                tagModel.setTagId(tagKey);
                tagModel.setTaggerId(mAuthUserId);
                tagModel.setTagName(entry.getTagName());
                tagModelList.add(tagModel);

            }

           sessionModel.setTags(tagModelList);

        }
        return sessionModel;
    }
}