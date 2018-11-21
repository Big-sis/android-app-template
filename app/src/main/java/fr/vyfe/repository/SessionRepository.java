package fr.vyfe.repository;


import android.os.Environment;

import java.io.File;
import java.util.Date;

import fr.vyfe.mapper.SessionMapper;
import fr.vyfe.model.SessionModel;

import static android.os.Environment.DIRECTORY_MOVIES;

public class SessionRepository extends FirebaseDatabaseRepository<SessionModel> {

    public SessionRepository(String companyId) {
        super(new SessionMapper(), companyId);
    }

    @Override
    protected String getRootNode() {
        return getCompany() + "/Sessions/";
    }


    @Override
    public String push(SessionModel sessionModel) {
        //Stockage dispo
        final long freeSpace = Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES).getFreeSpace();
        File file = new File(sessionModel.getDeviceVideoLink());
        long lengthFile = file.length();

        if (lengthFile >= freeSpace || lengthFile == 0) {
            file.delete();
            return null;
        } else {
            sessionModel.setDeviceVideoLink(file.getAbsolutePath());
            return super.push(sessionModel);
        }
    }
}
