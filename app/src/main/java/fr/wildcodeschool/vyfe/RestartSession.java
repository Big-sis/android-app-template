package fr.wildcodeschool.vyfe;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

//TODO : Classe à supprimer quand les données seront gérées par des viewModels
public class RestartSession implements Parcelable {

    private String nameTitleSession;
    private String idTagSet;

    protected RestartSession(Parcel in) {
        nameTitleSession = in.readString();
        idTagSet = in.readString();
    }

    public RestartSession(String nameTitleSession, String idTagSet) {
        this.nameTitleSession = nameTitleSession;
        this.idTagSet = idTagSet;
    }

    public static final Creator<RestartSession> CREATOR = new Creator<RestartSession>() {
        @Override
        public RestartSession createFromParcel(Parcel in) {
            return new RestartSession(in);
        }

        @Override
        public RestartSession[] newArray(int size) {
            return new RestartSession[size];
        }
    };

    public static String implementTitleGrid(String titleName) {

        if (titleName.contains("_")) {
            String[] parts = titleName.split("_");
            String part2 = parts[parts.length - 1];

            String[] first = titleName.split(part2);
            String part1 = first[0];

            try {
                int number = Integer.parseInt(part2);
                part2 = String.valueOf(number += 1);
                titleName = part1 + part2;
                //mSingletonSessions.setTitleSession(titleName);

            } catch (NumberFormatException nfe) {
                Log.d("format", "implementTitleGrid: " + nfe.getMessage());
                titleName += "_2";
                // mSingletonSessions.setTitleSession(titleName);
            }
        } else titleName += "_2";

        return titleName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameTitleSession);
        dest.writeString(idTagSet);
    }

    public String getNameTitleSession() {
        return nameTitleSession;
    }

    public void setNameTitleSession(String nameTitleSession) {
        this.nameTitleSession = nameTitleSession;
    }

    public String getIdTagSet() {
        return idTagSet;
    }

    public void setIdTagSet(String idTagSet) {
        this.idTagSet = idTagSet;
    }
}
