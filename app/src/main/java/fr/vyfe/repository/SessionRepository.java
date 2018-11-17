package fr.vyfe.repository;


import fr.vyfe.model.SessionModel;

public class SessionRepository extends FirebaseDatabaseRepository<SessionModel> {

    public SessionRepository(String companyId) {
        super(new SessionMapper(), companyId);
    }

    @Override
    protected String getRootNode() {
        return getCompany() + "/Sessions/";
    }

//    public SessionModel sessionMapper(SessionModel sessionModel) {
//        ArrayList<TagModel> mTagModels = sessionModel.getTags();
//
//        if (mTagModels != null) {
//
//            String mIdSession = dataBaseReference.push().getKey();
//            sessionModel.setIdSession(mIdSession);
//
//            sessionRef2.child(mIdSession).child("author").setValue(sessionModel.getAuthor());
//            sessionRef2.child(mIdSession).child("name").setValue(sessionModel.getName());
//            sessionRef2.child(mIdSession).child("idTagSet").setValue(sessionModel.getIdTagSet());
//            sessionRef2.child(mIdSession).child("pathApp").setValue(sessionModel.getDeviceVideoLink());
//            sessionRef2.child(mIdSession).child("idAndroid").setValue(sessionModel.getIdAndroid());
//            sessionRef2.child(mIdSession).child("date").setValue(sessionModel.getDate());
//
//            ArrayList<TagModel> tagModelList = new ArrayList<>();
//            for (TagModel entry : mTagModels) {
//                TagModel tagModel = new TagModel();
//
//                String tagKey = sessionRef2.child(mIdSession).child("Tags").push().getKey();
//                sessionRef2.child(mIdSession).child("Tags").child(tagKey).child("name").setValue(entry.getTagName());
//                sessionRef2.child(mIdSession).child("Tags").child(tagKey).child("color").setValue(entry.getColor());
//                sessionRef2.child(mIdSession).child("Tags").child(tagKey).child("Times").child(sessionModel.getAuthor()).setValue(entry.getTimes());
//
//                tagModel.setTimes(entry.getTimes());
//                tagModel.setColor(entry.getColor());
//                tagModel.setTagId(tagKey);
//                tagModel.setTaggerId(sessionModel.getAuthor());
//                tagModel.setTagName(entry.getTagName());
//                tagModelList.add(tagModel);
//
//            }
//
//            sessionModel.setTags(tagModelList);
//
//        }
//        return sessionModel;
//    }

}
